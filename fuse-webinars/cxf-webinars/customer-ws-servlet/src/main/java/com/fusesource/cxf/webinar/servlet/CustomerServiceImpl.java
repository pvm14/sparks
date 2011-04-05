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

package com.fusesource.cxf.webinar.servlet;

import com.fusesource.demo.customer.Customer;
import com.fusesource.demo.wsdl.customerservice.CustomerService;
import javax.jws.WebService;
import javax.xml.ws.Holder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebService (
  targetNamespace="http://demo.fusesource.com/wsdl/CustomerService/",
  name="CustomerService",
  serviceName="CustomerService",
  portName="SOAPOverHTTP"
)
public class CustomerServiceImpl implements CustomerService {

    private static final Logger log = LoggerFactory.getLogger(CustomerServiceImpl.class);

    public Customer lookupCustomer(String customerId) {
        log.debug("Returning information for customer '" + customerId + "'");
        Customer c = new Customer();

        c.setFirstName("Ade");
        c.setLastName("Trenaman");
        c.setId(customerId);
        c.setPhoneNumber("+353-1-01234567");

        return c;
    }

    public void updateCustomer(Customer cust) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void getCustomerStatus(String customerId, Holder<String> status, Holder<String> statusMessage) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


}
