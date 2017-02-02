package org.educama;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import java.util.LinkedHashMap;
import java.util.Map;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This class uses Spring Rest Docs to generate example requests which are embedded into asciidoc/rest-api.adoc.
 * The result is transformend to HTML and served at /docs/rest-api.html
 *
 * Documentation: http://docs.spring.io/spring-restdocs/docs/current/reference/html5
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = EducamaApplication.class, webEnvironment= SpringBootTest.WebEnvironment.DEFINED_PORT)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RestApiDocumentationTest {
	
	@Rule
	public final JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	private RestDocumentationResultHandler documentationHandler;

	private FieldDescriptor[] fieldDescriptorShipment;

	private FieldDescriptor[] fieldDescriptorTask;

	@Before
	public void setUp() {
		this.documentationHandler = document("{methodName}",
				preprocessRequest(
						prettyPrint(),
						removeHeaders("Host")),
				preprocessResponse(prettyPrint()));

		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(this.documentationHandler)
				.build();

		fieldDescriptorShipment = new FieldDescriptor[] {
				fieldWithPath("id").description("The unique database identifier"),
				fieldWithPath("trackingId").description("The unique business key of the shipment"),
				fieldWithPath("customer").description("The name of the customer"),
				fieldWithPath("senderAddress").description("The address of the sender"),
				fieldWithPath("receiverAddress").description("The address of the final receiver") };
		
		fieldDescriptorTask = new FieldDescriptor[] {
				fieldWithPath("createTime").description("The create time of the task"),
				fieldWithPath("trackingId").description("The unique business key of the shipment mapped to the task"),
				fieldWithPath("taskId").description("The Id of the task"),
				fieldWithPath("name").description("The task name"),
				fieldWithPath("description").description("The task description"),
				fieldWithPath("assignee").description("The assignee of the task"),
				fieldWithPath("customer").description("The shipments customer name") };
	}
		
	@Test
	public void createShipment() throws Exception {
		Map<String,String> shipment = new LinkedHashMap<>();
		shipment.put("customer", "NovaTec Consulting GmbH");
		shipment.put("senderAddress", "Dieselstr. 18/1, 70771 Leinfelden-Echterdingen, Germany");
		shipment.put("receiverAddress", "Santa Claus Main Post Office, FI-96930 Arctic Circle, Finland");
		this.mockMvc.perform(post("/educama/v1/shipments").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsBytes(shipment)))
				.andExpect(status().isCreated())
				.andDo(this.documentationHandler.document(
								requestFields(
								fieldWithPath("customer").description("The name of the customer"),
								fieldWithPath("senderAddress").description("The address of the sender"),
								fieldWithPath("receiverAddress").description("The address of the final receiver")
						),
						responseFields(fieldDescriptorShipment)
				));
	}
	
	@Test
	public void listShipment() throws Exception {
		this.mockMvc.perform(get("/educama/v1/shipments"))
			.andExpect(status().isOk())
			.andDo(this.documentationHandler.document(
					responseFields(
							fieldWithPath("shipments[]").description("An array of shipment objects")).andWithPrefix("shipments[].", fieldDescriptorShipment)));
	}
	
	@Test
	public void listTasks() throws Exception {
		this.mockMvc.perform(get("/educama/v1/tasks"))
			.andExpect(status().isOk())
			.andDo(this.documentationHandler.document(
					responseFields(
							fieldWithPath("tasks[]").description("An array of task objects")).andWithPrefix("tasks[].", fieldDescriptorTask)));
	}
}
