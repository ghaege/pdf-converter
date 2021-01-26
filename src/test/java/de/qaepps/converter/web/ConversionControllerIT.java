package de.qaepps.converter.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * integration tests with local installed LibreOffice
 * 
 * @precondition local installed LibreOffice
 * 
 * @author haege
 *
 */
@Tag("integrationTest")
@ActiveProfiles("test") // loads application-test.yml to overload application.yml
@SpringBootTest
class ConversionControllerIT { 

	private static final String REQUEST_URL = "/convert/toPdf";
    private static final String TARGET_DIR = "build/converted/byLocalLibreoffice";

    // controller under test
	@Autowired
	private ConversionController conversionController;

    private MockMvc mockMvc;
    private String targetMimeType;
    
    @BeforeEach
    public void before() {
    	// create mockMvc
    	mockMvc = MockMvcBuilders
    	        .standaloneSetup(conversionController)
    	        .build();
    	
    	targetMimeType = DefaultDocumentFormatRegistry.getFormatByExtension("pdf").getMediaType();
    }

    @ParameterizedTest
    @ValueSource(strings = { "any.docx", "any.dotx", "any.xlsx", "any.xltx"})
    @DisplayName("should convert office docs to pdf")
    void convertOfficeDocsToPdf(String filename) throws Exception {
		// prepare
    	Resource testFile = new ClassPathResource("/officefiles/" + filename);
		var multiPartFile = new MockMultipartFile("file", testFile.getFilename(), null, testFile.getInputStream());

		// test & validate
		MvcResult mvcResult = mockMvc.perform(multipart(REQUEST_URL).file(multiPartFile))
		.andExpect(status().isOk())
		.andExpect(content().contentType(targetMimeType))
		.andReturn()
		;
        
		saveContentToFile(filename, mvcResult);
	}
    
    @ParameterizedTest
    @ValueSource(strings = { "any.json", "any.txt", "any.xml", "any.xyz"})
    @DisplayName("should convert textfiles to pdf")
    void convertTextDocsToPdf(String filename) throws Exception {
		// prepare
    	Resource testFile = new ClassPathResource("/textfiles/" + filename);
		var multiPartFile = new MockMultipartFile("file", testFile.getFilename(), null, testFile.getInputStream());

		// test & validate
		MvcResult mvcResult = mockMvc.perform(multipart(REQUEST_URL).file(multiPartFile))
		.andExpect(status().isOk())
		.andExpect(content().contentType(targetMimeType))
		.andReturn()
		;
		
        saveContentToFile(filename, mvcResult);
	}

    @ParameterizedTest
    @ValueSource(strings = { "any.jar"})
    @DisplayName("should not convert unknown formats to pdf")
    void convertUnknownFormatsToPdf(String fileName) throws Exception {
		// prepare
    	Resource testFile = new ClassPathResource("/unknown/" + fileName);
		var multiPartFile = new MockMultipartFile("file", testFile.getFilename(), null, testFile.getInputStream());

		// test & validate
		mockMvc.perform(multipart(REQUEST_URL).file(multiPartFile))
		.andExpect(status().is5xxServerError())
		;
	}
    
	private void saveContentToFile(String filename, MvcResult mvcResult) throws IOException {
		FileUtils.writeByteArrayToFile(new File(TARGET_DIR, filename + ".pdf"), mvcResult.getResponse().getContentAsByteArray());
	}
    
}
