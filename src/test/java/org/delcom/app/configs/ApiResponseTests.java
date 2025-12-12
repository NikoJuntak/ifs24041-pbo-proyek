package org.delcom.app.configs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Test cases for ApiResponse class with comprehensive Jacoco coverage
 */
class ApiResponseTests {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== Constructor Tests ==========

    @Test
    @DisplayName("Default constructor should create empty ApiResponse")
    void testDefaultConstructor() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>();

        // Assert
        assertNotNull(response, "Response should not be null");
        assertEquals(0, response.getStatus(), "Default status should be 0");
        assertNull(response.getMessage(), "Default message should be null");
        assertNull(response.getData(), "Default data should be null");
    }

    @Test
    @DisplayName("Constructor with status and message only")
    void testConstructorWithoutData() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(200, "Success");

        // Assert
        assertEquals(200, response.getStatus(), "Status should be 200");
        assertEquals("Success", response.getMessage(), "Message should be 'Success'");
        assertNull(response.getData(), "Data should be null when not provided");
    }

    @Test
    @DisplayName("Constructor with all parameters")
    void testConstructorWithAllParameters() {
        // Arrange
        String expectedData = "Test Data";
        
        // Act
        ApiResponse<String> response = new ApiResponse<>(200, "Success", expectedData);

        // Assert
        assertEquals(200, response.getStatus(), "Status should be 200");
        assertEquals("Success", response.getMessage(), "Message should be 'Success'");
        assertEquals(expectedData, response.getData(), "Data should match provided value");
    }

    @ParameterizedTest
    @CsvSource({
        "200, OK",
        "404, Not Found",
        "500, Internal Server Error",
        "201, Created",
        "400, Bad Request"
    })
    @DisplayName("Constructor with different status codes and messages")
    void testConstructorWithVariousStatusCodes(int status, String message) {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(status, message);

        // Assert
        assertEquals(status, response.getStatus(), "Status should match input");
        assertEquals(message, response.getMessage(), "Message should match input");
        assertNull(response.getData(), "Data should be null");
    }

    // ========== Getter/Setter Tests ==========

    @Test
    @DisplayName("Test getters and setters with all combinations")
    void testGettersAndSetters() {
        // Arrange
        ApiResponse<Integer> response = new ApiResponse<>();

        // Act - Set values
        response.setStatus(404);
        response.setMessage("Not Found");
        response.setData(12345);

        // Assert - Get values
        assertEquals(404, response.getStatus(), "Status should be updated to 404");
        assertEquals("Not Found", response.getMessage(), "Message should be updated");
        assertEquals(12345, response.getData(), "Data should be updated to 12345");

        // Act - Update values
        response.setStatus(200);
        response.setMessage("OK");
        response.setData(null);

        // Assert - Verify updates
        assertEquals(200, response.getStatus(), "Status should be updated to 200");
        assertEquals("OK", response.getMessage(), "Message should be updated to OK");
        assertNull(response.getData(), "Data should be updated to null");
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 0, 100, 200, 404, 500, 999})
    @DisplayName("Test status setter with various integer values")
    void testStatusSetterWithVariousValues(int status) {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>();

        // Act
        response.setStatus(status);

        // Assert
        assertEquals(status, response.getStatus(), "Status should be set to " + status);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "  ", "\t", "\n", "Valid Message", "Special @#$% Chars", "123 Numbers"})
    @DisplayName("Test message setter with various string values")
    void testMessageSetterWithVariousValues(String message) {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>();

        // Act
        response.setMessage(message);

        // Assert
        assertEquals(message, response.getMessage(), "Message should be set to: '" + message + "'");
    }

    // ========== Data Type Tests ==========

    @Test
    @DisplayName("Test ApiResponse with String data type")
    void testApiResponseWithStringData() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(200, "OK", "String Data");

        // Assert
        assertInstanceOf(String.class, response.getData(), "Data should be String type");
        assertEquals("String Data", response.getData());
    }

    @Test
    @DisplayName("Test ApiResponse with Integer data type")
    void testApiResponseWithIntegerData() {
        // Arrange & Act
        ApiResponse<Integer> response = new ApiResponse<>(200, "OK", 42);

        // Assert
        assertInstanceOf(Integer.class, response.getData(), "Data should be Integer type");
        assertEquals(42, response.getData());
    }

    @Test
    @DisplayName("Test ApiResponse with null data")
    void testApiResponseWithNullData() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(200, "OK", null);

        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    @DisplayName("Test ApiResponse with Boolean data type")
    void testApiResponseWithBooleanData() {
        // Arrange & Act
        ApiResponse<Boolean> response = new ApiResponse<>(200, "OK", true);

        // Assert
        assertInstanceOf(Boolean.class, response.getData());
        assertTrue(response.getData());
    }

    @Test
    @DisplayName("Test ApiResponse with List data type")
    void testApiResponseWithListData() {
        // Arrange & Act
        ApiResponse<java.util.List<String>> response = new ApiResponse<>(
            200, "OK", java.util.List.of("item1", "item2", "item3")
        );

        // Assert
        assertInstanceOf(java.util.List.class, response.getData());
        assertEquals(3, response.getData().size());
        assertEquals("item2", response.getData().get(1));
    }

    // ========== JSON Serialization Tests ==========

    @Test
    @DisplayName("Test JSON serialization excludes null fields")
    void testJsonSerializationExcludesNullFields() throws JsonProcessingException {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>(200, "Success", null);
        
        // Act
        String json = objectMapper.writeValueAsString(response);
        
        // Assert
        assertFalse(json.contains("\"data\""), "JSON should not include data field when null");
        assertTrue(json.contains("\"status\":200"), "JSON should include status field");
        assertTrue(json.contains("\"message\":\"Success\""), "JSON should include message field");
        
        // Verify no trailing commas
        assertFalse(json.contains(",,"), "JSON should not have double commas");
    }

    @Test
    @DisplayName("Test JSON serialization includes non-null fields")
    void testJsonSerializationIncludesNonNullFields() throws JsonProcessingException {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>(200, "Success", "Test Data");
        
        // Act
        String json = objectMapper.writeValueAsString(response);
        
        // Assert
        assertTrue(json.contains("\"data\":\"Test Data\""), "JSON should include data field when not null");
        assertTrue(json.contains("\"status\":200"), "JSON should include status field");
        assertTrue(json.contains("\"message\":\"Success\""), "JSON should include message field");
    }

    @Test
    @DisplayName("Test JSON serialization with empty string message")
    void testJsonSerializationWithEmptyMessage() throws JsonProcessingException {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>(200, "", "Data");
        
        // Act
        String json = objectMapper.writeValueAsString(response);
        
        // Assert
        assertTrue(json.contains("\"message\":\"\""), "JSON should include empty message");
    }

    @Test
    @DisplayName("Test JSON serialization with zero status")
    void testJsonSerializationWithZeroStatus() throws JsonProcessingException {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>(0, "Zero Status", "Data");
        
        // Act
        String json = objectMapper.writeValueAsString(response);
        
        // Assert
        assertTrue(json.contains("\"status\":0"), "JSON should include zero status");
    }

    // ========== JSON Deserialization Tests ==========

    @Test
    @DisplayName("Test JSON deserialization with all fields")
    void testJsonDeserialization() throws JsonProcessingException {
        // Arrange
        String json = "{\"status\":200,\"message\":\"Success\",\"data\":\"Test Data\"}";
        
        // Act
        ApiResponse<String> response = objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, String.class));
        
        // Assert
        assertEquals(200, response.getStatus(), "Deserialized status should be 200");
        assertEquals("Success", response.getMessage(), "Deserialized message should match");
        assertEquals("Test Data", response.getData(), "Deserialized data should match");
    }

    @Test
    @DisplayName("Test JSON deserialization without data field")
    void testJsonDeserializationWithoutData() throws JsonProcessingException {
        // Arrange
        String json = "{\"status\":404,\"message\":\"Not Found\"}";
        
        // Act
        ApiResponse<String> response = objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, String.class));
        
        // Assert
        assertEquals(404, response.getStatus(), "Deserialized status should be 404");
        assertEquals("Not Found", response.getMessage(), "Deserialized message should match");
        assertNull(response.getData(), "Deserialized data should be null");
    }

    @Test
    @DisplayName("Test JSON deserialization with integer data")
    void testJsonDeserializationWithIntegerData() throws JsonProcessingException {
        // Arrange
        String json = "{\"status\":200,\"message\":\"OK\",\"data\":42}";
        
        // Act
        ApiResponse<Integer> response = objectMapper.readValue(json, 
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, Integer.class));
        
        // Assert
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertEquals(42, response.getData());
    }

    // ========== Edge Cases Tests ==========

    @Test
    @DisplayName("Test ApiResponse with null message")
    void testApiResponseWithNullMessage() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(500, null, "Error Data");

        // Assert
        assertEquals(500, response.getStatus(), "Status should be 500");
        assertNull(response.getMessage(), "Message should be null");
        assertEquals("Error Data", response.getData(), "Data should not be null");
    }

    @Test
    @DisplayName("Test ApiResponse with all null values")
    void testApiResponseWithAllNullValues() {
        // Arrange & Act
        ApiResponse<String> response = new ApiResponse<>(0, null, null);

        // Assert
        assertEquals(0, response.getStatus());
        assertNull(response.getMessage());
        assertNull(response.getData());
    }

    // ========== Multiple Instances Tests ==========

    @Test
    @DisplayName("Test multiple ApiResponse instances are independent")
    void testMultipleInstancesAreIndependent() {
        // Arrange
        ApiResponse<String> response1 = new ApiResponse<>(200, "Success", "Data1");
        ApiResponse<String> response2 = new ApiResponse<>(404, "Not Found", "Data2");
        
        // Act - Modify response1
        response1.setStatus(500);
        response1.setMessage("Internal Error");
        
        // Assert
        assertEquals(500, response1.getStatus(), "Response1 status should be modified");
        assertEquals("Internal Error", response1.getMessage(), "Response1 message should be modified");
        assertEquals("Data1", response1.getData(), "Response1 data should remain unchanged");
        
        assertEquals(404, response2.getStatus(), "Response2 status should remain unchanged");
        assertEquals("Not Found", response2.getMessage(), "Response2 message should remain unchanged");
        assertEquals("Data2", response2.getData(), "Response2 data should remain unchanged");
    }

    // ========== Object State Tests ==========

    @Test
    @DisplayName("Test object state consistency after multiple operations")
    void testObjectStateConsistency() {
        // Arrange
        ApiResponse<String> response = new ApiResponse<>();

        // Act - Multiple operations
        response.setStatus(100);
        response.setMessage("Continue");
        response.setData("Initial");
        
        // Assert - Verify initial state
        assertEquals(100, response.getStatus());
        assertEquals("Continue", response.getMessage());
        assertEquals("Initial", response.getData());

        // Act - Update operations
        response.setStatus(200);
        response.setMessage("OK");
        
        // Assert - Verify updated state
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertEquals("Initial", response.getData(), "Data should persist");

        // Act - Clear data
        response.setData(null);
        
        // Assert - Final state
        assertEquals(200, response.getStatus());
        assertEquals("OK", response.getMessage());
        assertNull(response.getData());
    }

    // ========== Helper Methods for Parameterized Tests ==========

    static Stream<Arguments> provideApiResponseTestData() {
        return Stream.of(
            arguments(200, "Success", "String Data", String.class),
            arguments(404, "Not Found", 404, Integer.class),
            arguments(500, "Error", true, Boolean.class),
            arguments(201, "Created", null, Object.class)
        );
    }

    @ParameterizedTest
    @MethodSource("provideApiResponseTestData")
    @DisplayName("Parameterized test with various data types")
    void testApiResponseWithVariousDataTypes(int status, String message, Object data, Class<?> expectedType) {
        // Arrange & Act
        ApiResponse<Object> response = new ApiResponse<>(status, message, data);

        // Assert
        assertEquals(status, response.getStatus());
        assertEquals(message, response.getMessage());
        
        if (data != null) {
            assertInstanceOf(expectedType, response.getData());
            assertEquals(data, response.getData());
        } else {
            assertNull(response.getData());
        }
    }

    // ========== Code Coverage Specific Tests ==========

    @Test
    @DisplayName("Test all code paths for constructor coverage")
    void testAllConstructorsForCoverage() {
        // Test all three constructors in one test
        ApiResponse<String> defaultResponse = new ApiResponse<>();
        ApiResponse<String> twoParamResponse = new ApiResponse<>(200, "OK");
        ApiResponse<String> threeParamResponse = new ApiResponse<>(200, "OK", "Data");

        // Verify all exist
        assertNotNull(defaultResponse);
        assertNotNull(twoParamResponse);
        assertNotNull(threeParamResponse);

        // Unique assertions for each
        assertEquals(0, defaultResponse.getStatus());
        assertEquals("OK", twoParamResponse.getMessage());
        assertEquals("Data", threeParamResponse.getData());
    }

    @Test
    @DisplayName("Test setters return void for coverage")
    void testSetterReturnTypes() throws NoSuchMethodException {
        // This test ensures we call all setters for coverage
        ApiResponse<String> response = new ApiResponse<>();
        
        // Call all setters
        response.setStatus(999);
        response.setMessage("Test");
        response.setData("Data");
        
        // Verify they were called
        assertEquals(999, response.getStatus());
        assertEquals("Test", response.getMessage());
        assertEquals("Data", response.getData());
        
        // This helps Jacoco see the setter calls
        response.setStatus(response.getStatus() + 1);
        response.setMessage(response.getMessage() + " Updated");
        response.setData(response.getData() + " Modified");
    }

    // ========== Integration Style Tests ==========

    @Test
    @DisplayName("Test complete lifecycle: create, modify, serialize, deserialize")
    void testCompleteLifecycle() throws JsonProcessingException {
        // 1. Create
        ApiResponse<String> original = new ApiResponse<>(201, "Created", "Resource ID: 123");
        
        // 2. Modify
        original.setStatus(200);
        original.setMessage("OK");
        original.setData("Updated Resource");
        
        // 3. Serialize
        String json = objectMapper.writeValueAsString(original);
        
        // 4. Deserialize
        ApiResponse<String> deserialized = objectMapper.readValue(json,
            objectMapper.getTypeFactory().constructParametricType(ApiResponse.class, String.class));
        
        // 5. Verify
        assertEquals(original.getStatus(), deserialized.getStatus());
        assertEquals(original.getMessage(), deserialized.getMessage());
        assertEquals(original.getData(), deserialized.getData());
        
        // 6. Modify deserialized
        deserialized.setStatus(204);
        deserialized.setMessage("No Content");
        deserialized.setData(null);
        
        // 7. Final verification
        assertEquals(204, deserialized.getStatus());
        assertEquals("No Content", deserialized.getMessage());
        assertNull(deserialized.getData());
    }
}