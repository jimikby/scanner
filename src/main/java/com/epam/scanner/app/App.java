package com.epam.scanner.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.domain.Artifact;
import com.epam.scanner.service.AppService;
import com.epam.scanner.utils.FileSaver;


public class App extends AppService{

	public static void main(String[] args) {
	
		if (args.length > 1) {
			setPathToTDP(args[1]); 
		} else {
			setPathToTDP(AppConfig.SCR_PATH);
		}
		
		List<Artifact> allTdpArtifacts = fingGroupId(parseGradleBuildForArtifacts());


		List<String> artifactIdsFromTxtFile = new ArrayList<>();
		
		try {
			artifactIdsFromTxtFile = parseTxtForArtifactId("*.txt");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	
		List<Artifact> tpdArtifactsFromTxtFile = findArtifacts(artifactIdsFromTxtFile,
			createMapArtifactIdAndArtifact(allTdpArtifacts));
	
		
		List<String> versionsFromTxtFile = parseTxtForVersion("*.txt");
	
		List<Artifact> pomArtfacts = AppService.findArtifacts(versionsFromTxtFile,
				createMapVersionAndArtifact(parseXMlForArtifacts("pom.xml")));
		
		List<Artifact> pomTdp4Artfacts = AppService.findArtifacts(versionsFromTxtFile,
				createMapVersionAndArtifact(parseXMlForArtifacts("pom_tdp_4_0.xml")));
		
		pomArtfacts.addAll(tpdArtifactsFromTxtFile);
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\tdp_components.txt", convertToGradleFormat(allTdpArtifacts));
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\all_pom_dependencies.txt", convertToGradleFormat(pomArtfacts));
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\all_pom_tdp4_dependencies.txt", convertToGradleFormat(pomTdp4Artfacts));
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\output.txt", convertToGradleFormat(pomArtfacts));
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\output_tdp_4.txt", convertToGradleFormat(pomTdp4Artfacts));
		

	}



}
