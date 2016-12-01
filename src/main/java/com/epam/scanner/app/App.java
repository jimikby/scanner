package com.epam.scanner.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.domain.Artifact;
import com.epam.scanner.service.AppService;
import com.epam.scanner.service.ArtifactService;
import com.epam.scanner.utils.DirectoryScanner;
import com.epam.scanner.utils.FileSaver;

public class App {
	
	@Autowired
	private AppService appService;

	@Autowired
	private ArtifactService artifactService;

	public static void main(String[] args) {
		
	     ApplicationContext ctx = 
	    	      new AnnotationConfigApplicationContext(AppConfig.class);

	     ArtifactService artifactService = ctx.getBean(ArtifactService.class);
	     AppService appService = ctx.getBean(AppService.class);
	     
		if (args.length > 1) {
			appService.setPathToTDP(args[1]);
		} else {
			appService.setPathToTDP(AppConfig.SCR_PATH);
		}
		//String command = "check-version";
		//String command = "check-version-umbrella";
		//String command = "description";
		//String command = "parsetxt";
		String command = "modules";
		if (!command.contains("check-version") && !command.equals("description")) {
		
		List<Artifact> allTdpArtifacts = appService.fingGroupId(appService.parseGradleBuildForArtifacts());
		List<Artifact> allPomArtifacts = appService.parseXMlForArtifacts("pom.xml");
		List<Artifact> allPomTDP4Artifacts = appService.parseXMlForArtifacts("pom_tdp_4_0.xml");
		
		artifactService.saveAll(allTdpArtifacts,"tdp"); //set type tdp
		artifactService.saveAll(allPomArtifacts,"pom"); //set type pom
		artifactService.saveAll(allPomTDP4Artifacts,"pom4"); //set type pom_tdp_4_0
		
		
		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\tmp.txt",
				appService.convertToGradleFormat((artifactService.getAll()), ""));

		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\tdp_components.txt",
				appService.convertToGradleFormat(allTdpArtifacts, "ALL"));

		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\all_pom_dependencies.txt",
				appService.convertToGradleFormat(allPomArtifacts, "ALL"));

		FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\all_pom_tdp4_dependencies.txt",
				appService.convertToGradleFormat(allPomTDP4Artifacts, "ALL"));

		System.out.println("Found " + allTdpArtifacts.size() + " TDP artifacts.");
		}
	
		switch (command) {

		case "tld": 
			String mask = "*.tld";
			List<String> tldFilePatterns = new ArrayList<>();
			appService.tldScan (mask, tldFilePatterns);
			break;

//		case "warlist":
//			makeWarListFilesForGradle(mapAllTdpArtifacts, mapAllPomTDP4Artifacts, mapAllPomArtifacts, "*list*.txt");
//			break;
			
		case "modules-inline":
			List<Artifact> pathFromTxtFile1;
			pathFromTxtFile1 = appService.parseFromModulesInlineList(AppConfig.LIST_JARS_INPUT + "\\modules.dep");
			List<Artifact> tpdArtifactsFromTxtPathFile1 = appService.findArtifactsByPath(pathFromTxtFile1);
			
			FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\modules.txt",appService.convertToGradleFormat(tpdArtifactsFromTxtPathFile1, ""));
			break;

		case "modules":
			List<Artifact> pathFromTxtFile;
			try {
				pathFromTxtFile = appService.parseFromModulesList("modules.dep");
				List<Artifact> tpdArtifactsFromTxtPathFile = appService.findArtifactsByPath(pathFromTxtFile);
				
				FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\modules.txt",appService.convertToGradleFormat(tpdArtifactsFromTxtPathFile, ""));
			
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			break;
			
		case "check-version": {
			List<String> masks = new ArrayList<>();
			masks.add("version: '1.0'");
			masks.add("version: '4.0.0'");
			masks.add("version: \"4.0.0\"");
			masks.add("version: \"1.0\"");
			List<File> searchfiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.filterFiles(searchfiles,masks);
			break;}
		
		case "check-version-umbrella": {
			List<String> masks = new ArrayList<>();
			masks.add("version: '1.0'");
			masks.add("version: '4.0.0'");
			masks.add("version: \"4.0.0\"");
			masks.add("version: \"1.0\"");
			List<File> searchfiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.filterFilesUmbrella(searchfiles,masks);
			break;}
		
		case "description": {
			List<File> searchfiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.addDescription(searchfiles);
			break;}
		
		case "parsetxt":
			List<Artifact> artifactIdsFromTxtFile = new ArrayList<>();

			try {
				artifactIdsFromTxtFile = appService.parseTxtForArtifactId("*.txt");
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			List<Artifact> tpdArtifactsFromTxtFile = appService.findArtifactsByArtifactId(artifactIdsFromTxtFile, "TDP");
			List<Artifact> versionsFromTxtFile = appService.parseTxtForVersion("*.txt");
			List<Artifact> pomArtfacts = appService.findArtifactsByVersion(versionsFromTxtFile, "pom.xml");
			List<Artifact> pomTdp4Artfacts = appService.findArtifactsByVersion(versionsFromTxtFile, "pom_TDP_4.xml");

			pomArtfacts.addAll(tpdArtifactsFromTxtFile);

			FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\output" + (System.currentTimeMillis() / 1000) + ".txt",
					appService.convertToGradleFormat(pomArtfacts, ""));
			FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\output" + (System.currentTimeMillis() / 1000) + "_tr.txt",
					appService.convertToGradleFormat(pomArtfacts, "ALL"));

			FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\output_tdp_4.txt",
					appService.convertToGradleFormat(pomTdp4Artfacts, ""));

		}

	}

	private void makeWarListFilesForGradle(String mask) {
		List<File> searchfiles = appService.collectFiles(mask);

		for (File searchfile : searchfiles) {

			List<Artifact> tpdArtifactsFromTxt = new ArrayList<>();
			List<Artifact> pomArtifactsFromTxt = new ArrayList<>();

			try {
				tpdArtifactsFromTxt = appService.parseTxtForArtifactId(searchfile);
			} catch (IOException e) {
				e.printStackTrace();
			}
			pomArtifactsFromTxt = appService.parseTxtForVersion(searchfile);

			List<Artifact> tpdArtifactsFromTxtFile = appService.findArtifactsByArtifactId(tpdArtifactsFromTxt,
					"TDP");

			List<Artifact> pomArtfacts = appService.findArtifactsByVersion(pomArtifactsFromTxt, "pom.xml");
			List<Artifact> pomTdp4Artfacts = appService.findArtifactsByVersion(pomArtifactsFromTxt,"pom_TDP_4.xml");

			pomArtfacts.addAll(tpdArtifactsFromTxtFile);
			FileSaver.save(searchfile.getAbsolutePath().replace(".txt", "_auto.gradle"),
					appService.convertToGradleFormat(pomArtfacts, ""));
			FileSaver.save(searchfile.getAbsolutePath().replace(".txt", "_TDP4_auto.gradle"),
					appService.convertToGradleFormat(pomTdp4Artfacts, ""));
		}
		System.out.println("Found " + searchfiles.size() + " artifacts.");
	}


}