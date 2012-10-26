/**
 * Copyright 2012, Board of Regents of the University of
 * Wisconsin System. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Board of Regents of the University of Wisconsin
 * System licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package edu.wisc.doit.tcrypt;

import java.util.Iterator;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@org.springframework.stereotype.Controller
public class HomeController {

	private IKeysKeeper keysKeeper;

	@Autowired
	public HomeController(IKeysKeeper keysKeeper){
		this.keysKeeper = keysKeeper;
	}

	@RequestMapping("/")
	public ModelAndView handleRequest() throws Exception {
		ModelAndView modelAndView = new ModelAndView("tcryptCreateKey");
		Set<String> serviceNames = keysKeeper.getListOfServiceNames();
		
		modelAndView.addObject("serviceNames", formatForJavaScript(serviceNames));
		return modelAndView;
	}
	

	private String formatForJavaScript(Set<String> serviceNames) {
		Iterator<String> iterator = serviceNames.iterator();
		String commaSeparated = "[ '" + iterator.next() + "'";
		for (; iterator.hasNext();) 
			commaSeparated =  commaSeparated + ", '" + iterator.next() + "'";
		commaSeparated = commaSeparated+ "]";
		return commaSeparated;
	}
}
