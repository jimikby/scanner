package com.epam.scanner.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.domain.Artifact;
import com.epam.scanner.exception.ParserXmlException;
import com.epam.scanner.utils.DirectoryScanner;
import com.epam.scanner.utils.FileReader;
import com.epam.scanner.utils.parser.ArtifactBuilder;
import com.epam.scanner.utils.parser.common.ParcerXml;
import com.epam.scanner.utils.parser.common.ParserBuilder;
import com.epam.scanner.utils.parser.common.ParserFactory;
import com.epam.scanner.utils.parser.common.ParserType;
import com.epam.scanner.utils.parser.enums.ParsedType;

public class AppService {
	
	private static String pathToTDP;
	
	public static void setPathToTDP(String path) {
		pathToTDP = path;
	}

	public static String convertToGradleFormat(List<Artifact> artifacts) {

		String txtFile = "";
		for (Artifact artifact : artifacts) {

			if (artifact.getGroupId() != null) {

				txtFile += "group: '" + artifact.getGroupId() + "', name: '" + artifact.getArtifactId()
						+ "', version: property('" + artifact.getVersion() + "')";

				if (artifact.getExclude()) {
					txtFile += ", transitive: false";
				}
			} else {
				txtFile += artifact.getArtifactId();
			}
			if (artifact.getPath() != null) {
				txtFile += " //" + artifact.getPath();
			}

			txtFile += "\r\n";
		}
		return txtFile;
	}

	public static List<Artifact> findArtifacts(List<String> searchParams, Map<String, Artifact> map) {
		List<Artifact> artifacts = new ArrayList<>();
		for (String version : searchParams) {
			Artifact artifact = map.get(version);
			if (artifact != null) {
				artifacts.add(artifact);
			} else { artifacts.add(new Artifact ( null,  null,  version,  version,  false, null,null));} 
		}
		return artifacts;
	}

	public static Map<String, Artifact> createMapVersionAndArtifact(List<Artifact> parsedItems) {
		Map<String, Artifact> pom = new HashMap<>();

		for (Artifact artifact : parsedItems) {
			pom.put(artifact.getVersion(), artifact);
		}
		return pom;
	}

	public static List<Artifact> parseXMlForArtifacts(String fileName) {
		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), fileName);

		ParcerXml parser = ParserFactory.create(ParserType.STAX);

		ParserBuilder builder = new ArtifactBuilder().add(ParsedType.DEPENDENCY);

		List<Artifact> parsedItems = new ArrayList<>();
		
		

		for (File file : searchfiles) {
			try {
				parsedItems.addAll((List<Artifact>) parser.create(file, builder));
			} catch (ParserXmlException e) {
				e.printStackTrace();
			} 

		}
		return parsedItems;
	}

	public static List<String> parseTxtForVersion(String mask) {
		List<String> versionCollection = new ArrayList<>();

		List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), mask);

		for (File file : searchfiles) {

			String text = FileReader.read(file);

			Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String tokenKey = matcher.group(1);
				versionCollection.add(tokenKey);
			}

		}
		return versionCollection;
	}

	public static List<Artifact> parseGradleBuildForArtifacts() {

		List<Artifact> artifacts = new ArrayList<>();

		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), "build.gradle");

		for (File file : searchfiles) {

			artifacts.addAll(checkForArtifactIdAndGroupId(file));

		}
		return artifacts;
	}

	private static List<Artifact> checkForArtifactIdAndGroupId(File file) {
		List<Artifact> artifacts = new ArrayList<>();
		Artifact artifact = new Artifact();
		String text = FileReader.read(file);
		String path = file.getPath();

		text = text.replaceAll("'", "\"").replaceAll(" ", "").replaceAll("	", "").replaceAll("=", "")
				.replaceAll("\\n", "").replaceAll("\\r", "");

		Matcher matcherGroupId = Pattern.compile("group\"(.*?)\"").matcher(text);
		while (matcherGroupId.find()) {

			artifact.setGroupId(matcherGroupId.group(1));
			getVersionFromGroupId(artifact);

		}

		Pattern pattern = Pattern.compile("archivesBaseName\"(.*?)\"");
		Matcher matcherArtifactId = pattern.matcher(text);
		while (matcherArtifactId.find()) {
			String artifactId = matcherArtifactId.group(1);

			artifactId = artifactId.replace("\"", "").replace("archivesBaseName", "");

			path = path.replace(pathToTDP, "").replace("build.gradle", "").replace("\\", "");

			artifact.setPath(path);
			artifact.setArtifactId(artifactId);

		}

		if (artifact.getArtifactId() != null || artifact.getGroupId() != null) {

			artifacts.add(artifact);

		}

		return artifacts;
	}

	private static void getVersionFromGroupId(Artifact artifact) {
		String group = artifact.getGroupId();
		if (group != null) {
			String[] version = StringUtils.split(group, ".");

			artifact.setVersion("component." + version[3] + ".version");
		} else {
			// System.out.println(artifact);
		}

	}

	public static List<Artifact> fingGroupId(List<Artifact> artifacts) {

		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), "settings.gradle");

		for (File file : searchfiles) {

			String text = FileReader.read(file);
			String path = file.getPath();

			for (Artifact artifact : artifacts) {

				if (artifact.getPath() != null) {
					Pattern pattern = Pattern.compile(artifact.getPath());

					Matcher matcher = pattern.matcher(text);
					while (matcher.find()) {
						File buildGradleFile = new File(path.replace("settings", "build"));

						List<Artifact> artifactGroupCheck = checkForArtifactIdAndGroupId(buildGradleFile);

						if (!artifactGroupCheck.isEmpty()) {

							artifact.setGroupId(artifactGroupCheck.get(0).getGroupId());
						} else {

							// System.out.println(buildGradleFile);

						}

						getVersionFromGroupId(artifact);

					}
				} else {
					// System.out.println(artifact);
				}
			}
		}

		return artifacts;
	}

	public static List<String> parseTxtForArtifactId(String mask) throws IOException {
		List<String> artifactIdCollection = new ArrayList<>();

		List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), mask);

		for (File file : searchfiles) {
			
			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			while ((strLine = br.readLine()) != null)   {
			 
			 if (strLine.replace("$", "") == strLine && strLine.replace(".jar","") != strLine) {
				 artifactIdCollection.add(strLine.replace(".jar","")); 
			 }
			}

			br.close();

		}
		return artifactIdCollection;
	}
	
	public static Map<String, Artifact> createMapArtifactIdAndArtifact(List<Artifact> allTdpArtifacts) {
		Map<String, Artifact> mapArtifactIdAndArtifact = new HashMap<>();

		for (Artifact artifact : allTdpArtifacts) {
			mapArtifactIdAndArtifact.put(artifact.getArtifactId(), artifact);
		}
		return mapArtifactIdAndArtifact;
	}
}
