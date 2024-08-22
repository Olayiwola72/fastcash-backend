package com.fastcash.moneytransfer.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

public record GoogleCredentialsRequest(
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "user credential", example = "ssassasasaxa11.eyJpc3MiOiJodHRwcz")
		@NotEmpty
		String credential, 
		
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "client id", example = "8q92jgoh58ev.apps.googleusercontent.com")
		@NotEmpty
		String clientId, 
		
		
		@Schema(accessMode = Schema.AccessMode.WRITE_ONLY, description = "select by", example = "btn")
		@NotEmpty
		String select_by
	){
	
	@JsonCreator
    public static GoogleCredentialsRequest create(
       @JsonProperty("credential") String credential,
       @JsonProperty("clientId") String clientId,
       @JsonProperty("select_by") String select_by
       ) {
			
		return new GoogleCredentialsRequest(credential, clientId, select_by);
    }
}
