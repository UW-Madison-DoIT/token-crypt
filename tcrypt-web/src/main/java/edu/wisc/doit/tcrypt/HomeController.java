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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import java.util.Iterator;
import java.util.Set;

@org.springframework.stereotype.Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

	private IKeysKeeper keysKeeper;

	@Autowired
	public HomeController(IKeysKeeper keysKeeper){
		this.keysKeeper = keysKeeper;
	}

	@RequestMapping("/index.html")
	public ModelAndView handleRequest() throws Exception
    {
		ModelAndView modelAndView = new ModelAndView("tcryptCreateKey");
		Set<String> serviceNames = keysKeeper.getListOfServiceNames();

        if (!serviceNames.isEmpty())
        {
            modelAndView.addObject("serviceNames", formatForJavaScript(serviceNames));
        }

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
