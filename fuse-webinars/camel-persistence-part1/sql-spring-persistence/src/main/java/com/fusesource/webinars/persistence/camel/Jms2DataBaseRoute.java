package com.fusesource.webinars.persistence.camel;

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.camel.*;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.FileIdempotentRepository;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.spi.Registry;


public class Jms2DataBaseRoute extends RouteBuilder {

	@EndpointInject(ref = "outdb")
    Endpoint endpointDatasource;

	@EndpointInject(ref = "endpointJms")
	Endpoint corepointJms;

	private List<String> report = new ArrayList<String>();

    // private IdempotentRepository myRepo = new MemoryIdempotentRepository();
    private IdempotentRepository myRepo;

    private File store = new File("storage/idempotentfilestore.dat");


    @Override
    public void configure() throws Exception {

        report.add("01");
        report.add("2011-03-21");
        report.add("Charles");
        report.add("Moulliard");
        report.add("Incident-01");
        report.add("This is a report incident for camel-01");
        report.add("cmoulliard@fusesource.com");
        report.add("+111 10 20 300");

        // Does not delete store
        if (!store.exists()) {
            store.createNewFile();
        }

        myRepo = FileIdempotentRepository.fileIdempotentRepository(store);

        from("file://datainsert")
        //.setHeader("messageId").simple("${bean:util?method=increaseCounter}")
        .setHeader("messageId").simple("${header.CamelFileName}")
        .log("File created")
        .to(corepointJms);

        from(corepointJms)
        .transacted("PROPAGATION_REQUIRED") // Use JMS Tx Manager
        .setBody().constant(report)
        .to("direct:A")
        .process(new org.apache.camel.Processor() {
            public void process(Exchange exch) throws Exception {
                if (myRepo.contains("test1")) {
                    throw new Exception("Cannot connect  ....");
                }
            }
        })
        .log("End of the process ...");

        from("direct:A")
        //.transacted("PROPAGATION_REQUIRED") // Use DataSource Tx Manager
        .log(">>>> Message ID : ${header.messageId}")
        .idempotentConsumer(header("messageId"), myRepo).eager(false)
        .to(endpointDatasource)
        .log("Record should be inserted one time .....");
    }
}
