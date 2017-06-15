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
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

public class App {

	@Autowired
	private AppService appService;

	@Autowired
	private ArtifactService artifactService;

	public static void main(String[] args) {

		ApplicationContext ctx = new AnnotationConfigApplicationContext(AppConfig.class);

		ArtifactService artifactService = ctx.getBean(ArtifactService.class);
		AppService appService = ctx.getBean(AppService.class);

		if (args.length > 1) {
			appService.setPathToTDP(args[1]);
		} else {
			appService.setPathToTDP(AppConfig.SCR_PATH);
		}
		// String command = "check-version";
		// String command = "check-version-umbrella";
		// String command = "description";
		// String command = "3dpaty";
		// String command = "modules";
		// String command = "replace";
		String command = "parsetxt";
		//String command = "findproperties";

		if (!command.contains("check-version") && !command.equals("description") && !command.equals("replace")) {

			List<Artifact> allTdpArtifacts = appService.fingGroupId(appService.parseGradleBuildForArtifacts());
			List<Artifact> allPomArtifacts = appService.parseXMlForArtifacts("pom*.xml");
			List<Artifact> allPomTDP4Artifacts = appService.parseXMlForArtifacts("pom_tdp_4_0.xml");

			artifactService.saveAll(allTdpArtifacts, "tdp"); // set type tdp
			artifactService.saveAll(allPomArtifacts, "pom"); // set type pom
			artifactService.saveAll(allPomTDP4Artifacts, "pom4"); // set type
																	// pom_tdp_4_0

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
			appService.tldScan(mask, tldFilePatterns);
			break;

		// case "warlist":
		// makeWarListFilesForGradle(mapAllTdpArtifacts, mapAllPomTDP4Artifacts,
		// mapAllPomArtifacts, "*list*.txt");
		// break;

		case "modules-inline":
			List<Artifact> pathFromTxtFile1;
			pathFromTxtFile1 = appService.parseFromModulesInlineList(AppConfig.LIST_JARS_INPUT + "\\modules.dep");
			List<Artifact> tpdArtifactsFromTxtPathFile1 = appService.findArtifactsByPath(pathFromTxtFile1);

			FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\modules.txt",
					appService.convertToGradleFormat(tpdArtifactsFromTxtPathFile1, ""));
			break;

		case "modules":
			List<Artifact> pathFromTxtFile;
			try {
				pathFromTxtFile = appService.parseFromModulesList("modules.dep");
				List<Artifact> tpdArtifactsFromTxtPathFile = appService.findArtifactsByPath(pathFromTxtFile);

				FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\modules.txt",
						appService.convertToGradleFormat(tpdArtifactsFromTxtPathFile, ""));

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
			List<File> filteredFiles = appService.filterFilesDep(searchfiles, masks);
			break;
		}

		case "check-version-umbrella": {
			List<String> masks = new ArrayList<>();
			masks.add("version: '1.0'");
			masks.add("version: '4.0.0'");
			masks.add("version: \"4.0.0\"");
			masks.add("version: \"1.0\"");
			List<File> searchfiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.filterFilesUmbrella(searchfiles, masks);
			break;
		}
		
		case "replace": {
			List<String> masks = new ArrayList<>();
			masks.add("'../00006435/build/package.properties'");
			String replace = "\"${appServerConfigDir}/package.properties\"";
			List<File> searchFiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.filterFiles(searchFiles, masks);
			appService.replaceAndSave(filteredFiles, masks, replace);
			break;
		}

		case "3dpaty": {
			List<File> searchFiles = appService.collectFiles("*.gradle");
			List<Artifact> artifacts = appService.collectDependencies(searchFiles);
			appService.saveFiles(artifacts);

			List<File> searchFilesGroovy = appService.collectFilesPath("*.groovy", "D:\\gradle-plugins");
			List<Artifact> artifactsPlugin = appService.collectDependencies(searchFilesGroovy);
			appService.saveFiles(artifactsPlugin, AppConfig.TDP_LIBS + "/", "gradle-plugins");

			break;
		}


		case "findproperties": {
			List<Artifact> artifactIdsFromTxtFile = new ArrayList<>();
			List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), "*.txt");
			for (File file : searchfiles) {
				artifactIdsFromTxtFile = appService.parseArtifactFromVersionedJarList("*.txt", file);
				System.out.println(artifactIdsFromTxtFile);
				FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\" + file.getName().replace(".txt", "_new.txt"),
						appService.convertToGradleFormat(artifactIdsFromTxtFile, ""));
			}
			break;
		}

		case "description": {
			List<File> searchfiles = appService.collectFiles("build.gradle");
			List<File> filteredFiles = appService.addDescription(searchfiles);
			break;
		}

		case "parsetxt": {
			List<Artifact> artifactIdsFromTxtFile = new ArrayList<>();

			List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), "*.txt");
			for (File file : searchfiles) {
				try {
					artifactIdsFromTxtFile = appService.parseTxtForArtifactId("*.txt", file);
					

					List<Artifact> tpdArtifactsFromTxtFile = appService
							.findArtifactsByArtifactId(artifactIdsFromTxtFile, "TDP");

					List<Artifact> versionsFromTxtFile = appService.parseTxtForVersion("*.txt", file);

					List<Artifact> pomArtfacts = appService.findArtifactsByVersion(versionsFromTxtFile, "pom*.xml");
					List<Artifact> pomTdp4Artfacts = appService.findArtifactsByVersion(versionsFromTxtFile,
							"pom_TDP_4.xml");

					pomArtfacts.addAll(tpdArtifactsFromTxtFile);

					FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\" + file.getName().replace(".txt", "_new.txt"),
							appService.convertToGradleFormat(pomArtfacts, ""));
					FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\" + file.getName().replace(".txt", "_tr.txt"),
							appService.convertToGradleFormat(pomArtfacts, "ALL"));

					FileSaver.save(AppConfig.LIST_JARS_OUTPUT + "\\" + file.getName().replace(".txt", "_TDP_4.txt"),
							appService.convertToGradleFormat(pomTdp4Artfacts, ""));

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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

			List<Artifact> tpdArtifactsFromTxtFile = appService.findArtifactsByArtifactId(tpdArtifactsFromTxt, "TDP");

			List<Artifact> pomArtfacts = appService.findArtifactsByVersion(pomArtifactsFromTxt, "pom*.xml");
			List<Artifact> pomTdp4Artfacts = appService.findArtifactsByVersion(pomArtifactsFromTxt, "pom_TDP_4.xml");

			pomArtfacts.addAll(tpdArtifactsFromTxtFile);
			FileSaver.save(searchfile.getAbsolutePath().replace(".txt", "_auto.gradle"),
					appService.convertToGradleFormat(pomArtfacts, ""));
			FileSaver.save(searchfile.getAbsolutePath().replace(".txt", "_TDP4_auto.gradle"),
					appService.convertToGradleFormat(pomTdp4Artfacts, ""));
		}
		System.out.println("Found " + searchfiles.size() + " artifacts.");
	}

}