//package com.epam.scanner.service;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.commons.lang3.StringUtils;
//
//import com.epam.scanner.domain.Artifact;
//import com.epam.scanner.utils.DirectoryScanner;
//import com.epam.scanner.utils.FileReader;
//
//public class UmbrellaService {
//
//	public static Artifact parseSuperUmbrellas() {
//		Artifact levelOne = new Artifact();
//		List<File> searchfiles = DirectoryScanner.listf(new File(AppService.getPathToTDP()), "settings-*.txt");
//		for (File file : searchfiles) {
//			System.out.println(file);
//			String text = FileReader.read(file);
//			String path = file.getPath().replace(AppService.getPathToTDP(), "").replace("settings-", "")
//					.replace("\\", "").replace(".txt", "");
//
//			text = text.replace("'", "").replace(" ", "").replace("	", "");
//			String[] umbrellaNames = StringUtils.split(text, ",");
//			Artifact levelTwo = new Artifact();
//			for (String name : umbrellaNames) {
//				System.out.println("umbrella " + name);
//				Artifact levelThree = new Artifact();
//				levelTwo.setName(name);
//				levelTwo.setPath(name);
//				File buildFile = new File(AppService.getPathToTDP() + "\\" + name + "\\" + "settings.gradle");
//					
//					
//					levelThree = AppService.addParamsToArtifact(text,
//							buildFile.getAbsolutePath(), levelThree);
//					levelThree.setArtifacts(AppService.readSettingsGradleFile(name, buildFile));
//		
//					
//					for (Artifact artifact : levelThree.getArtifacts()) {
//						System.out.println("4 " + artifact.getPath());	
//					}
//				
//	
//				
//				//levelTwo.setArtifacts(levelThree);
//				
//		
//			
//			}
//			
//			for (Artifact artifact : levelTwo.getArtifacts()) {
//				System.out.println("2 " + artifact.getPath());	
//			}
//			
//			//levelOne.setArtifacts(levelTwo);
//		}
//		
//		return levelOne;
//	}
//
//}
