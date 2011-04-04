/**
 * Copyright 2011 FuseSource
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.fusesource.customer.ws;

import com.fusesource.demo.customer.Customer;
import com.fusesource.demo.wsdl.customerservice.CustomerService;
import java.security.Principal;
import javax.annotation.Resource;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import javax.xml.ws.WebServiceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService(targetNamespace = "http://demo.fusesource.com/wsdl/CustomerService/",
name = "CustomerService",
serviceName = "CustomerService",
portName = "SOAPOverHTTP")
public class CustomerServiceImpl implements CustomerService {

  private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);
  
  @Resource
  private WebServiceContext wsc;

  public Customer lookupCustomer(String customerId) {

    Principal caller = wsc.getUserPrincipal();
    log.info("Caller is " + ((caller == null) ? " <no-principal> " : caller.getName()));
    
    log.info("Returning information for customer '" + customerId + "'");
    Customer c = new Customer();

    c.setFirstName("Ade");
    c.setLastName("Trenaman");
    c.setId(customerId);
    c.setPhoneNumber("+353-1-01234567");

    return c;
  }

  public void updateCustomer(Customer cust) {
    Principal caller = wsc.getUserPrincipal();
    log.info("Caller is " + ((caller == null) ? " <no-principal> " : caller.getName()));
    
    log.info("Update customer called.");
  }

  public void getCustomerStatus(String customerId, Holder<String> status, Holder<String> statusMessage) {
    Principal caller = wsc.getUserPrincipal();
    
    log.info("Caller is " + ((caller == null) ? " <no-principal> " : caller.getName()));
    log.info("Getting statys for custoemr " + customerId);
    status.value = "asleep";
    statusMessage.value = "ZZzzzzz";
  }
}
