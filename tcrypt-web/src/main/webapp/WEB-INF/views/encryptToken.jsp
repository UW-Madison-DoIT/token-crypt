<%--

    Copyright 2012, Board of Regents of the University of
    Wisconsin System. See the NOTICE file distributed with
    this work for additional information regarding copyright
    ownership. Board of Regents of the University of Wisconsin
    System licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<%@ include file="/WEB-INF/views/taglibs.jsp"%>
<z:layout pageTitle="Token Encryption">
    <script type="text/javascript">
        (function( $ ) {
            $.widget( "ui.combobox", {
                _create: function() {
                    var input,
                            that = this,
                            wasOpen = false,
                            select = this.element.hide(),
                            selected = select.children( ":selected" ),
                            value = selected.val() ? selected.text() : "",
                            wrapper = this.wrapper = $( "<span>" )
                                    .addClass( "ui-combobox" )
                                    .insertAfter( select );

                    function removeIfInvalid( element ) {
                        var value = $( element ).val(),
                                matcher = new RegExp( "^" + $.ui.autocomplete.escapeRegex( value ) + "$", "i" ),
                                valid = false;
                        select.children( "option" ).each(function() {
                            if ( $( this ).text().match( matcher ) ) {
                                this.selected = valid = true;
                                return false;
                            }
                        });

                        if ( !valid ) {
                            // remove invalid value, as it didn't match anything
                            $( element )
                                    .val( "" )
                                    .attr( "title", value + " didn't match any item" )
                                    .tooltip( "open" );
                            select.val( "" );
                            setTimeout(function() {
                                input.tooltip( "close" ).attr( "title", "" );
                            }, 2500 );
                            input.data( "ui-autocomplete" ).term = "";
                        }
                    }

                    input = $( "<input>" )
                            .appendTo( wrapper )
                            .val( value )
                            .attr( "title", "" )
                            .addClass( "ui-state-default ui-combobox-input" )
                            .autocomplete({
                                delay: 0,
                                minLength: 0,
                                source: function( request, response ) {
                                    var matcher = new RegExp( $.ui.autocomplete.escapeRegex(request.term), "i" );
                                    response( select.children( "option" ).map(function() {
                                        var text = $( this ).text();
                                        if ( this.value && ( !request.term || matcher.test(text) ) )
                                            return {
                                                label: text.replace(
                                                        new RegExp(
                                                                "(?![^&;]+;)(?!<[^<>]*)(" +
                                                                        $.ui.autocomplete.escapeRegex(request.term) +
                                                                        ")(?![^<>]*>)(?![^&;]+;)", "gi"
                                                        ), "<strong>$1</strong>" ),
                                                value: text,
                                                option: this
                                            };
                                    }) );
                                },
                                select: function( event, ui ) {
                                    ui.item.option.selected = true;
                                    that._trigger( "selected", event, {
                                        item: ui.item.option
                                    });
                                },
                                change: function( event, ui ) {
                                    if ( !ui.item ) {
                                        removeIfInvalid( this );
                                    }
                                }
                            })
                            .addClass( "ui-widget ui-widget-content ui-corner-left" );

                    input.data( "ui-autocomplete" )._renderItem = function( ul, item ) {
                        return $( "<li>" )
                                .append( "<a>" + item.label + "</a>" )
                                .appendTo( ul );
                    };

                    var mySelect = document.getElementById("serviceNames");
                    var set = false;
                    var selectedServiceName = $("#selectedServiceName").val();
                    if(selectedServiceName.length > 0) {
			        	for(var i=0; i < mySelect.length; i++) {
							var option = mySelect[i];
							//alert("option: " + option.value);
							//alert("selectedServiceName: " + $("#selectedServiceName").val());
							if(option.value == selectedServiceName) {
								input.val(selectedServiceName);
								option.selected = true;
								set = true;
								break;
							}
						}
                    }
		        	if(!set) {
		        		input.val("");
		        	}

                    $( "<a>" )
                            .attr( "tabIndex", -1 )
                            .attr( "title", "Show All Items" )
                            .tooltip()
                            .appendTo( wrapper )
                            .button({
                                icons: {
                                    primary: "ui-icon-triangle-1-s"
                                },
                                text: false
                            })
                            .removeClass( "ui-corner-all" )
                            .addClass( "ui-corner-right ui-combobox-toggle" )
                            .mousedown(function() {
                                wasOpen = input.autocomplete( "widget" ).is( ":visible" );
                            })
                            .click(function() {
                                input.focus();

                                // close if already visible
                                if ( wasOpen ) {
                                    return;
                                }

                                // pass empty string as value to search for, displaying all results
                                input.autocomplete( "search", "" );
                            });

                    input.tooltip({
                        tooltipClass: "ui-state-highlight"
                    });
                },

                _destroy: function() {
                    this.wrapper.remove();
                    this.element.show();
                }
            });
        })( jQuery );

        $(function() {
            $( "#serviceNames" ).combobox();
        });
    </script>
    
    <script type="text/javascript">
        function doAjaxPost() {
	        // get the form values
	        var serviceName = $('#serviceNames').val();
	        var text = $('#text').val();
	        var file = $('#fileToEncrypt').val();
			if(serviceName.length == 0) {
				showError("<spring:message code='error.serviceNameRequired'/>");
				
			} else if (text.length == 0 && file.length == 0) {
				showError("<spring:message code='error.textOrFileRequired' />");
			} else {
				if(text.length != 0) { //text encrypt via ajax
			        $.ajax({
			          type: "POST",
			          url: "${pageContext.request.contextPath}/apps/encryptAjax",
			          data: "serviceKeyName=" + serviceName + "&unencryptedText=" + encodeURIComponent(text),
			          success: function(response){
			            $('#encryptedText').val(response.encryptedText);
			          },
			          error: function(e){
			       	  	var response = jQuery.parseJSON(e.responseText);
			       	 	showError(response.errorMessage);
			          }
			        });
				} else { //file encryption via form submission
					$('#selectedServiceName').val(serviceName);
					$('#fileEncryptionForm').submit();
				}
        	}
        }
        
     	// run the currently selected effect
	    function showError(errorMessage) {
	 	  // run the effect
	 	  $( "#ajaxError").html(errorMessage);
	      $( "#ajaxErrorDialog" ).show( "drop", {}, 500);
	      
	    };
        </script>
        
        <script type="text/javascript">
        
        	function clearAll(toClearText) {
        		$("#encryptedText").val("");
        		$("img.check").hide();
        		$("img.check2").hide();
        		$( "#ajaxErrorDialog" ).hide("drop", {}, 500, null);
        		if(toClearText) {
        			$("#text").val("");
        		}
        	}
        
	        $(document).ready(function() {	
	        	//On change of text in the text field clear out the encrypted text to reduce confusion
	        	$("input#text").bind('keydown',function () {
	        		clearAll(false);
	        	});
	        	$( "#ajaxErrorDialog" ).hide();
	        });
	        
        </script>
      
    <div id="ajaxErrorDialog" title="Error">  
		<p id="ajaxError" class="warning">&nbsp;</p>
	</div>

    <div id="stylizedForm" class="userForms">
        
        <label>Service Name</label>
        <select id="serviceNames" name="serviceNames">
            <option value="">&nbsp;</option>
            <core:forEach var="name" items="${serviceNames}">
                <option value ="<core:out value="${name}"/>"><core:out value="${name}"/></option>
            </core:forEach>
        </select>
        <div id="textEncryption">
        <label>Text</label> <input type="text" id="text" name="text" /> 
        <label>Encrypted Text</label>
        <input type="text" id="encryptedText" />
		<a href="#" id="copy-encrypted"><img src="${ pageContext.request.contextPath }/images/Clipboard-icon.png" alt="Copy to clipboard"/></a><img class="check2" src="${ pageContext.request.contextPath }/images/checkmark.png" style="display : none" alt='copied' />
		<script type="text/javascript">
		$(window).load(function() {
			$("#copy-encrypted").zclip({
			    path: "${ pageContext.request.contextPath }/js/ZeroClipboard.swf",
			    copy:function(){return $('input#encryptedText').val();
				},
				afterCopy:function(){
		            $(this).next('.check2').show();
		        }
			});
		});
	
		</script>
		</div>
		
		<form name="encryptToken" id="fileEncryptionForm" action="${pageContext.request.contextPath}/apps/encryptFile" autocomplete="off" enctype="multipart/form-data" method="POST"> 
			<div id="fileEncryption">
				<label>File</label>
				<input type="file" name="fileToEncrypt" id="fileToEncrypt" onclick="clearAll(true);return true;" />
			</div>
			<div>
			<button onclick="doAjaxPost(); return false;" >Encrypt</button>&nbsp;<a href="#" id="copyshare" title="Share with friends."><img src="${ pageContext.request.contextPath }/images/Link-icon.png" /></a><img class="check" src="${ pageContext.request.contextPath }/images/checkmark.png" style="display : none" alt='copied' />
			</div>
			
			<input type='hidden' name="selectedServiceName" value="${selectedServiceName}" id="selectedServiceName"/>
            
            <script lang='javascript'>
            $(window).load(function() {	
            	$("#copyshare").zclip({
				    path: "${ pageContext.request.contextPath }/js/ZeroClipboard.swf",
				    copy:function(){return (window.location.host + '${pageContext.request.contextPath}/apps/encrypt/' + $('#serviceNames option:selected').val());},
					afterCopy:function(){
			            $(this).next('.check').show();
			        }
				});
            });	
		    </script>
	    </form>
    </div>
</z:layout>