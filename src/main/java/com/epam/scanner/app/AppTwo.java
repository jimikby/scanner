//package com.epam.scanner.app;
//
//import java.util.List;
//
//import com.epam.scanner.config.AppConfig;
//import com.epam.scanner.domain.Artifact;
//import com.epam.scanner.service.AppService;
//import com.epam.scanner.service.UmbrellaService;
//import com.epam.scanner.utils.FileSaver;
//import com.fasterxml.jackson.core.JsonGenerationException;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//public class AppTwo {
//
//	public static void main(String[] args) {
//		
//		
//		AppService.setPathToTDP(AppConfig.SCR_PATH);
//	
//		Artifact artifacts = UmbrellaService.parseSuperUmbrellas();	
//
//		ObjectMapper mapper = new ObjectMapper();
//
////		try {
////			String jsonInString = mapper.writeValueAsString(artifacts);
//			System.out.println(artifacts.size());
//			System.out.println(artifacts.getAllPath());
////			 FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\artifacts_tree.json",jsonInString);
////		} catch (JsonProcessingException e) {
////			e.printStackTrace();
////		}
////		
//		
//		
//	}
//}
