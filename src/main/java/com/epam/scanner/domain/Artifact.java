package com.epam.scanner.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.epam.scanner.utils.parser.common.ParsedItem;

public class Artifact implements AbstractDomainEntity, ParsedItem {

	private String groupId;
	private String artifactId;
	private String version;
	private Boolean exclude;
	private String path;
	private Map <String,Artifact> dependencies;
	private List <Artifact> artifacts = new ArrayList<>();
	private String name;
	private String classifier;
	private String extension;
	private String type;
	
	public int size() {
		int size = this.artifacts.size();
		for (Artifact artifact : artifacts) {
			if (artifact.getArtifacts() != null) {
			size += artifact.getArtifacts().size();
			}
		}
		return size;
	}
	
	public List <String> getAllPath() {
		List <String> paths = new ArrayList<>();
		for (Artifact artifact : artifacts) {
			paths.add(artifact.getPath());
		}
		return paths;
	}
	
	public List <String> getAllNames() {
		List <String> names = new ArrayList<>();
		for (Artifact artifact : artifacts) {
			names.add(artifact.getName());
		}
		return names;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getArtifactId() {
		return artifactId;
	}

	public void setArtifactId(String artifactId) {
		this.artifactId = artifactId;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Boolean getExclude() {
		return exclude;
	}

	public void setExclude(Boolean exclude) {
		this.exclude = exclude;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Map<String, Artifact> getDependencies() {
		return dependencies;
	}

	public void setDependencies(Map<String, Artifact> dependencies) {
		this.dependencies = dependencies;
	}

	public List<Artifact> getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(List<Artifact> artifacts) {
		this.artifacts = artifacts;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((artifacts == null) ? 0 : artifacts.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((dependencies == null) ? 0 : dependencies.hashCode());
		result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
		result = prime * result + ((extension == null) ? 0 : extension.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((version == null) ? 0 : version.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Artifact other = (Artifact) obj;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (artifacts == null) {
			if (other.artifacts != null)
				return false;
		} else if (!artifacts.equals(other.artifacts))
			return false;
		if (classifier == null) {
			if (other.classifier != null)
				return false;
		} else if (!classifier.equals(other.classifier))
			return false;
		if (dependencies == null) {
			if (other.dependencies != null)
				return false;
		} else if (!dependencies.equals(other.dependencies))
			return false;
		if (exclude == null) {
			if (other.exclude != null)
				return false;
		} else if (!exclude.equals(other.exclude))
			return false;
		if (extension == null) {
			if (other.extension != null)
				return false;
		} else if (!extension.equals(other.extension))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		if (version == null) {
			if (other.version != null)
				return false;
		} else if (!version.equals(other.version))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Artifact [groupId=");
		builder.append(groupId);
		builder.append(", artifactId=");
		builder.append(artifactId);
		builder.append(", version=");
		builder.append(version);
		builder.append(", exclude=");
		builder.append(exclude);
		builder.append(", path=");
		builder.append(path);
		builder.append(", dependencies=");
		builder.append(dependencies);
		builder.append(", artifacts=");
		builder.append(artifacts);
		builder.append(", name=");
		builder.append(name);
		builder.append(", classifier=");
		builder.append(classifier);
		builder.append(", extension=");
		builder.append(extension);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}

}