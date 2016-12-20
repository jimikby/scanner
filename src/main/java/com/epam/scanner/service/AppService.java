package com.epam.scanner.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.scanner.config.AppConfig;
import com.epam.scanner.domain.Artifact;
import com.epam.scanner.exception.ParserXmlException;
import com.epam.scanner.utils.DirectoryScanner;
import com.epam.scanner.utils.FileReader;
import com.epam.scanner.utils.FileSaver;
import com.epam.scanner.utils.manager.GradleConfigManager;
import com.epam.scanner.utils.parser.ArtifactBuilder;
import com.epam.scanner.utils.parser.common.ParcerXml;
import com.epam.scanner.utils.parser.common.ParserBuilder;
import com.epam.scanner.utils.parser.common.ParserFactory;
import com.epam.scanner.utils.parser.common.ParserType;
import com.epam.scanner.utils.parser.enums.ParsedType;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;

@Service
public class AppService {

	@Autowired
	private ArtifactService artifactService;

	private String pathToTDP;

	public void setPathToTDP(String path) {
		pathToTDP = path;
	}

	public String getPathToTDP() {
		return pathToTDP;
	}

	public String convertToGradleFormat(List<Artifact> artifacts, String type) {
		
		String text = "";

	// "configurations {\r\n	warlibs.transitive = false\r\n}\r\n\r\ndependencies {\r\n";
		for (Artifact artifact : artifacts) {
			String txtFile = "";
			if (artifact != null ) {

				if (artifact.getGroupId() != null && !artifact.getGroupId().equals("pricing")) {

					if (type.equals("ALL")) {
						txtFile += "\tcomplie ";
					}

					txtFile += "group: '" + artifact.getGroupId() + "', name: '" + artifact.getArtifactId()
							+ "', version: property('" + artifact.getVersion() + "')";

					if (artifact.getExtension() != null && artifact.getExtension() != "jar") {
						txtFile += ", ext: '" + artifact.getExtension() + "'";
						System.out.println(artifact.getExtension());
					}

					if (artifact.getExclude() != null && artifact.getExclude() && type.equals("ALL")) {
						txtFile += ", transitive: false";
					}
				} else {
					txtFile += artifact.getArtifactId();
					if (type.equals("TDP_ALL")) {
						System.out.println("Found Artifact without umbrella: " + artifact.getArtifactId() + " //"
								+ artifact.getPath());
					}
				}
				if (artifact.getPath() != null && type.equals("ALL")) {

					// if (artifact.getType() != null &&
					// artifact.getType().equals("tdp")) {
					txtFile += " //" + artifact.getPath();
					// }

				}
			} else {
				txtFile += null;
			}
			text = text.replace(txtFile+ "\r\n", "");
			text = text + txtFile + "\r\n";
		}
		//txtFile += "}";
		return text;
	}

	public List<Artifact> findArtifactsByArtifactId(List<Artifact> incompleteArtifacts, String message) {
		List<Artifact> artifacts = new ArrayList<>();
		for (Artifact incompleteArtifact : incompleteArtifacts) {
			String value = incompleteArtifact.getArtifactId();
			List<Artifact> newArtifacts = artifactService.takeByArtifactId(value);

			if (newArtifacts != null) {
				artifacts.addAll(newArtifacts);
			} else {
				// if (!message.equals("pom_TDP_4.xml")) {
				// System.out.println("Artifact doesn't found in " + message + "
				// : " + value);
				// try {
				// artifacts.add(checkManualDependencies(value));
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// }
			}
		}
		return artifacts;
	}

	public List<Artifact> findArtifactsByVersion(List<Artifact> incompleteArtifacts, String message) {
		List<Artifact> artifacts = new ArrayList<>();
		for (Artifact incompleteArtifact : incompleteArtifacts) {
			String value = incompleteArtifact.getVersion();

			List<Artifact> newArtifacts = artifactService.takeByVersion(value);

			if (newArtifacts != null) {
				
				artifacts.addAll(newArtifacts);

			} else {
				// if (!message.equals("pom_TDP_4.xml")) {
				// System.out.println("Artifact doesn't found in " + message
				// + " : " + value);
				// try {
				// artifacts.add(checkManualDependencies(value));
				// } catch (IOException e) {
				// e.printStackTrace();
				// }
				// }
			}
		}
		return artifacts;
	}

	public List<Artifact> parseXMlForArtifacts(String fileName) {
		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), fileName);

		ParcerXml parser = ParserFactory.create(ParserType.STAX);

		ParserBuilder builder = new ArtifactBuilder().add(ParsedType.DEPENDENCY);

		List<Artifact> artifacts = new ArrayList<>();

		for (File file : searchfiles) {
			try {
				List<Artifact> parsedArtifacts = (List<Artifact>) parser.create(file, builder);

				for (Artifact artifact : parsedArtifacts) {
					artifact.setPath(file.getAbsolutePath().replace(pathToTDP + "\\", ""));
				}

				artifacts.addAll(parsedArtifacts);

			} catch (ParserXmlException e) {
				e.printStackTrace();
			}
		}
		return artifacts;
	}

	public List<Artifact> parseTxtForVersion(String mask) {
		List<Artifact> artifacts = new ArrayList<>();
		List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), mask);

		for (File file : searchfiles) {
			String text = FileReader.read(file);
			Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				String version = matcher.group(1);
				Artifact artifact = new Artifact();
				artifact.setVersion(version);
				artifacts.add(artifact);
			}
		}
	
		
		return artifacts;
	}

	public List<Artifact> parseTxtForVersion(File searchfile) {
		List<Artifact> artifacts = new ArrayList<>();

		String text = FileReader.read(searchfile);

		Pattern pattern = Pattern.compile("\\$\\{([^}]*)\\}");
		Matcher matcher = pattern.matcher(text);
		while (matcher.find()) {
			String version = matcher.group(1);
			Artifact artifact = new Artifact();
			artifact.setVersion(version);
			artifacts.add(artifact);
		}

		return artifacts;
	}

	public List<Artifact> parseGradleBuildForArtifacts() {
		List<Artifact> artifacts = new ArrayList<>();
		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), "build.gradle");
		for (File file : searchfiles) {
			artifacts.addAll(checkForArtifactIdAndGroupId(file));
		}
		return artifacts;
	}

	private List<Artifact> checkForArtifactIdAndGroupId(File file) {
		List<Artifact> artifacts = new ArrayList<>();
		Artifact artifact = new Artifact();
		String text = FileReader.read(file);
		String path = file.getPath();

		text = text.replaceAll("'", "\"").replaceAll(" ", "").replaceAll("	", "").replaceAll("=", "")
				.replaceAll("\\n", "").replaceAll("\\r", "");

		Matcher matcherGroupId = Pattern.compile("group\"(.*?)\"").matcher(text);
		while (matcherGroupId.find()) {

			artifact.setGroupId(matcherGroupId.group(1));
			artifact = getVersionFromGroupId(artifact);

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

		} else {
			System.out.println(artifact + " " + path);
		}
		return artifacts;
	}

	private Artifact getVersionFromGroupId(Artifact artifact) {
		String group = artifact.getGroupId();
		String ver = null;
		if (group != null) {
			String[] version = StringUtils.split(group, ".");
			if (version.length >= 3) {
				ver = version[3];
				if (ver.equals("handlers") || ver.equals("base")) {
					ver = ver + "." + version[4];
				}

			} else {
				//
			}

			artifact.setVersion("component." + ver + ".version");
		} else {
			// System.out.println(artifact);
		}
		return artifact;

	}

	public List<Artifact> fingGroupId(List<Artifact> artifacts) {

		List<File> searchfiles = DirectoryScanner.listf(new File(pathToTDP), "settings.gradle");

		for (File file : searchfiles) {
			String text = FileReader.read(file);
			String path = file.getPath();

			for (Artifact artifact : artifacts) {
				artifact = addParamsToArtifact(text, path, artifact);
			}
		}

		return artifacts;
	}

	public Artifact addParamsToArtifact(String text, String path, Artifact artifact) {
		if (artifact.getPath() != null) {
			Pattern pattern = Pattern.compile(artifact.getPath());

			Matcher matcher = pattern.matcher(text);
			while (matcher.find()) {
				File buildGradleFile = new File(path.replace("settings", "build"));

				List<Artifact> artifactGroupCheck = checkForArtifactIdAndGroupId(buildGradleFile);

				if (!artifactGroupCheck.isEmpty() && artifactGroupCheck.get(0).getGroupId() != null) {

					artifact.setGroupId(artifactGroupCheck.get(0).getGroupId());
				} else {

					// System.out.println(buildGradleFile);

				}

				artifact = getVersionFromGroupId(artifact);

			}
		} else {
			// System.out.println(artifact);
		}
		return artifact;
	}

	public List<Artifact> parseTxtForArtifactId(String mask) throws IOException {
		List<Artifact> artifacts = new ArrayList<>();

		List<File> searchfiles = DirectoryScanner.listf(new File(AppConfig.LIST_JARS_INPUT), mask);

		for (File file : searchfiles) {

			FileInputStream fstream = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine;

			while ((strLine = br.readLine()) != null) {

				Artifact artifact = new Artifact();

				if (!strLine.contains("${") && strLine.contains(".")) {

					String[] strLineArray = StringUtils.split(strLine, ".");

					artifact.setArtifactId(strLineArray[0]);

					if (strLineArray.length > 1) {
						artifact.setExtension(strLineArray[1]);
					} else {
						System.out.println("Invalid input data: " + strLine);
					}

					artifacts.add(artifact);
				}
			}
			br.close();
		}
		return artifacts;
	}

	public List<Artifact> parseTxtForArtifactId(File file) throws IOException {
		List<Artifact> artifacts = new ArrayList<>();

		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		while ((strLine = br.readLine()) != null) {

			if (strLine.replace("$", "") == strLine) {
				Artifact artifact = new Artifact();

				String[] strLineArray = StringUtils.split(strLine, ".");

				artifact.setArtifactId(strLineArray[0]);
				artifact.setExtension(strLineArray[1]);
				artifacts.add(artifact);
			}
		}

		br.close();
		return artifacts;

	}

	public Artifact checkManualDependencies(String name) throws IOException {
		Artifact artifact = new Artifact();

		artifact.setArtifactId(name);

		FileInputStream fstream = new FileInputStream("D:\\manual\\manual_dep.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		while ((strLine = br.readLine()) != null) {

			String[] line = StringUtils
					.split(strLine.replace(" ", "").replace("'", "").replace("property(", "").replace(")", ""), "=");

			if (name.equals(line[0])) {

				String[] artifactsParam = StringUtils.split(line[1], ",");
				artifact.setGroupId(StringUtils.split(artifactsParam[0], ":")[1]);
				artifact.setArtifactId(StringUtils.split(artifactsParam[1], ":")[1]);
				artifact.setVersion(StringUtils.split(artifactsParam[2], ":")[1]);
			}
		}

		br.close();

		return artifact;
	}

	public List<Artifact> parseFromModulesList(String fileName) throws IOException {
		List<Artifact> artifacts = new ArrayList<>();
		File file = new File(AppConfig.LIST_JARS_INPUT + "\\" + fileName);

		FileInputStream fstream = new FileInputStream(file);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

		String strLine;

		while ((strLine = br.readLine()) != null) {
			Artifact artifact = new Artifact();
			artifact.setPath(strLine);
			artifacts.add(artifact);
		}

		br.close();

		return artifacts;
	}

	List<Artifact> readSettingsGradleFile(String fileName, File file) {
		List<Artifact> artifacts = new ArrayList<>();
		String text;
		if (file.exists()) {
			text = FileReader.read(file);
			text = text.replace("includeFlat", "").replace("'", "").replace(" ", "").replace("\"", "").replace("	",
					"");

			String[] paths = StringUtils.split(text.replace(" ", ""), ",");

			for (String path : paths) {

				File newFile = new File(pathToTDP + "\\" + path + "\\build.gradle");
				if (newFile.exists()) {

					for (Artifact artifact : checkForArtifactIdAndGroupId(newFile)) {
						artifacts.add(artifact);
					}

				} else {
					Artifact newArtifact = new Artifact();
					newArtifact.setPath(path);
					artifacts.add(newArtifact);
				}
			}

			return artifacts;
		} else {
			Artifact newArtifact = new Artifact();
			newArtifact.setPath(fileName);
			artifacts.add(newArtifact);
			return artifacts;
		}
	}

	public List<Artifact> findArtifactsByPath(List<Artifact> incompleteArtifacts) {
		List<Artifact> artifacts = new ArrayList<>();
		for (Artifact incompleteArtifact : incompleteArtifacts) {
			String path = incompleteArtifact.getPath();
			List<Artifact> newArtifacts = artifactService.takeByPath(path);
			if (!newArtifacts.isEmpty()) {
				artifacts.addAll(newArtifacts);
			} else {

				Artifact tmpArtifact = new Artifact();
				tmpArtifact.setPath(path);
				artifacts.add(tmpArtifact);
				System.out.println("Artifact doesn't found: " + path);
			}
			;
		}
		return artifacts;
	}

	public void tldScan(String mask, List<String> tldFilePatterns) {
		List<File> searchfiles = DirectoryScanner.listf(new File(getPathToTDP()), mask);
		tldFilePatterns.add("(\\\\config\\\\)");
		tldFilePatterns.add("(\\\\src\\\\tld\\\\)");

		String inputPath = AppConfig.LIST_JARS_INPUT + "\\files.tld";

		Map<String, String> tldsPaths = collectTlds(searchfiles, tldFilePatterns);

		System.out.println(tldsPaths);

		List<Artifact> artfacts = createArtifactsFormTlds(tldsPaths, inputPath);

		String tldText = convertToGradleFormat(artfacts, "tld");

		System.out.println(tldText);
	}

	private Map<String, String> collectTlds(List<File> searchfiles, List<String> tldFilePatterns) {
		Map<String, String> tldsPaths = new HashMap<>();
		for (File file : searchfiles) {

			for (String pattern : tldFilePatterns) {

				Pattern patt = Pattern.compile(pattern);

				Matcher matcher = patt.matcher(file.getAbsolutePath());
				while (matcher.find()) {

					String[] array = StringUtils.split(file.getAbsolutePath(), "\\");
					tldsPaths.put(array[array.length - 1], array[2]);
				}
			}
		}
		return tldsPaths;
	}

	private List<Artifact> createArtifactsFormTlds(Map<String, String> tldsPaths, String inputPath) {

		List<Artifact> artfacts = new ArrayList<>();

		FileInputStream fstream;
		try {
			fstream = new FileInputStream(inputPath);
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

			String strLine = null;

			while ((strLine = br.readLine()) != null) {
				if (tldsPaths.get(strLine) != null) {
					artfacts.addAll(artifactService.takeByPath(tldsPaths.get(strLine)));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return artfacts;
	}

	public List<Artifact> parseFromModulesInlineList(String fileName) {
		List<Artifact> artifacts = new ArrayList<>();
		File file = new File(fileName);
		String text;
		if (file.exists()) {
			text = FileReader.read(file);
			text = text.replace("includeFlat", "").replace("'", "").replace(" ", "").replace("\"", "").replace("	",
					"");

			String[] paths = StringUtils.split(text.trim(), ",");

			for (String path : paths) {
				System.out.println(path);
				artifacts.addAll(artifactService.takeByPath(path));

			}
		}

		return artifacts;
	}

	public List<File> collectFiles(String mask) {
		List<File> searchfiles;
		searchfiles = DirectoryScanner.listf(new File(pathToTDP), mask);
		return searchfiles;
	}

	public List<File> filterFiles(List<File> searchfiles, List<String> masks) {
		List<File> filterFiles = new ArrayList<>();
		List<String> gradleProps = Collections.list(GradleConfigManager.getKeys());
		String spOne = "group: 'com.datalex.tdp.";
		String spTwo = "', name:";

		System.out.println(gradleProps);

		System.out.println(filterFiles.size());
		for (File file : searchfiles) {
			String textOriginal = FileReader.read(file);
			String text = textOriginal;

			Splitter splitter = Splitter.on(CharMatcher.anyOf("\n")).omitEmptyStrings();

			List<String> result = splitter.splitToList(text);

			for (String mask : masks) {
			int i = 0;
				for (String dep : result) {
					i++;
					if (dep.contains(mask)) {
						splitter = Splitter.on(spOne);
						List<String> resultOne = splitter.splitToList(dep);
						splitter = Splitter.on(spTwo);
						if (resultOne.size() > 1) {
							List<String> resultTwo = splitter.splitToList(resultOne.get(1));
							String resTwo = resultTwo.get(0);
							
							if (resTwo.contains("ui.")) {
								resTwo = "ui";
							}
							
							if (resTwo.contains("core.")) {
								resTwo = "core";
							}
							
							if (resTwo.contains("fare.")) {
								resTwo = "fare";
							}
							
							if (resTwo.contains("services.")) {
								resTwo = "services";
							}
							
							if (resTwo.contains("handlers.redemption")) {
								resTwo = "handlers.redemption";
							}
							
							if (resTwo.contains("handlers.land")) {
								resTwo = "handlers.land";
							}
							
							if (resTwo.contains("handlers.air")) {
								resTwo = "handlers.air";
							}
							
							if (resTwo.contains("payment")) {
								resTwo = "payment";
							}
							
							String ver = "version: property('component." + resTwo + ".version')";

							String version = "component." + resTwo + ".version";
							 System.out.println(version);
							if (gradleProps.contains(version)) {
								 String newDep = resultOne.get(0) + spOne + resultTwo.get(0) + spTwo + resultTwo.get(1).replace(mask, ver);
								textOriginal = textOriginal.replace(dep, newDep);
								
							}
							
						}
					}
				}
			}
			 FileSaver.save(file.toString(), textOriginal);
		}
		return filterFiles;
	}
	
	public List<File> filterFilesUmbrella(List<File> searchfiles, List<String> masks) {
		List<File> filterFiles = new ArrayList<>();
		List<String> gradleProps = Collections.list(GradleConfigManager.getKeys());
		
		String spOne = "group='";
		String spTwo = "'";
		String tab = "";
		
		System.out.println(gradleProps);
		System.out.println(filterFiles.size());
		
		for (File file : searchfiles) {
			String textOriginal = FileReader.read(file);
			String text = textOriginal;

			Splitter splitter = Splitter.on(CharMatcher.anyOf("\n")).omitEmptyStrings();

			List<String> result = splitter.splitToList(text);

	
			int i = 0;
				for (String dep : result) {
					i++;
	
						splitter = Splitter.on(spOne);
						List<String> resultOne = splitter.splitToList(dep.replace("\"", "'").replace(" ", ""));
						splitter = Splitter.on(spTwo);
						if (resultOne.size() > 1) {
							List<String> resultTwo = splitter.splitToList(resultOne.get(1));
							String resTwo = resultTwo.get(0).replace("com.datalex.tdp.", "");
							
							if (resTwo.contains("ui.")) {
								resTwo = "ui";
							}
							
							if (resTwo.contains("core")) {
								resTwo = "core";
							}
							
							if (resTwo.contains("fare")) {
								resTwo = "fare";
							}
							
							if (resTwo.contains("tools")) {
								resTwo = "tools";
							}
							
							if (resTwo.contains("services")) {
								resTwo = "services";
							}
							if (resTwo.contains("product.config")) {
								resTwo = "product.config";
							}
							
							if (resTwo.contains("handlers.redemption")) {
								resTwo = "handlers.redemption";
							}
							
							if (resTwo.contains("handlers.land")) {
								resTwo = "handlers.land";
							}
							
							if (resTwo.contains("handlers.air")) {
								resTwo = "handlers.air";
							}
							
							if (resTwo.contains("payment")) {
								resTwo = "payment";
							}	
							String ver = "version: property('component." + resTwo + ".version')";
							String version = "component." + resTwo + ".version";

							if (gradleProps.contains(version)) {
				
								
								if (result.get(i).contains("version")) {
								if (dep.contains("\t") || dep.contains("    ")) { tab = "\t";} else { tab = ""; }
								String newDep = tab + "version = property('" + version + "')";
								textOriginal = textOriginal.replace(result.get(i), newDep);
								FileSaver.save(file.toString(), textOriginal);	
								//System.out.println(file + " " + textOriginal);
								}

							}
						}
					}

		}
		return filterFiles;
	}


	public List<File> addDescription(List<File> searchfiles) {
		List<File> filterFiles = new ArrayList<>();
		int i = 0;
		for (File file : searchfiles) {
			String archivesBaseName = null;
			String textOriginal = FileReader.read(file);
			String text = textOriginal;
			text = text.replace("description =", "description=").replace("\"", "'");
			Splitter splitter = Splitter.on("description=");
			List<String> result = splitter.splitToList(text);
			if (result.size() == 1) {
				splitter = Splitter.on(CharMatcher.anyOf("\n\r")).omitEmptyStrings();
				List<String> lines = splitter.splitToList(text);
				String description = lines.get(0).replace("//", "");
				if (!lines.get(0).equals(description)) {
					for (String line : lines) {
						String lineRep = line.replace("archivesBaseName =", "archivesBaseName=");
						if (lineRep.contains("archivesBaseName=")) {
							archivesBaseName = line;

							File fileBuildXml = new File(file.getAbsolutePath().replace(".gradle", "/build.xml"));

							FileInputStream fstream;
							try {
								fstream = new FileInputStream(fileBuildXml);

								BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

								String strLine = br.readLine();
								splitter = Splitter.on("name=\"");
								List<String> resultSplitName = splitter.splitToList(strLine);
								if (resultSplitName.size() > 1) {
									splitter = Splitter.on("\"");
									List<String> resultSplit = splitter.splitToList(resultSplitName.get(1));
									description = resultSplit.get(0);
								}

								br.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}

					if (archivesBaseName != null && description != null) {
						String descriptionLine = archivesBaseName + "\n" + "description = '" + description.trim() + "'";
						textOriginal = textOriginal.replace(archivesBaseName, descriptionLine);

						FileSaver.save(file.toString(), textOriginal);
					}
				} else {
					System.out.println(file + " - doesn't found comment");
				}
			}
		}
		return filterFiles;
	}
}