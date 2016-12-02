package com.epam.scanner.domain;

public class Dependency {
	private String configuration;
	private Artifact artifact;
	private String ext;
	private String classifier;
	private Artifact parentArtifact;
	
	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public Artifact getArtifact() {
		return artifact;
	}

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}

	public String getClassifier() {
		return classifier;
	}

	public void setClassifier(String classifier) {
		this.classifier = classifier;
	}

	public Artifact getParentArtifact() {
		return parentArtifact;
	}

	public void setParentArtifact(Artifact parentArtifact) {
		this.parentArtifact = parentArtifact;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((artifact == null) ? 0 : artifact.hashCode());
		result = prime * result + ((classifier == null) ? 0 : classifier.hashCode());
		result = prime * result + ((configuration == null) ? 0 : configuration.hashCode());
		result = prime * result + ((ext == null) ? 0 : ext.hashCode());
		result = prime * result + ((parentArtifact == null) ? 0 : parentArtifact.hashCode());
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
		Dependency other = (Dependency) obj;
		if (artifact == null) {
			if (other.artifact != null)
				return false;
		} else if (!artifact.equals(other.artifact))
			return false;
		if (classifier == null) {
			if (other.classifier != null)
				return false;
		} else if (!classifier.equals(other.classifier))
			return false;
		if (configuration == null) {
			if (other.configuration != null)
				return false;
		} else if (!configuration.equals(other.configuration))
			return false;
		if (ext == null) {
			if (other.ext != null)
				return false;
		} else if (!ext.equals(other.ext))
			return false;
		if (parentArtifact == null) {
			if (other.parentArtifact != null)
				return false;
		} else if (!parentArtifact.equals(other.parentArtifact))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Dependency [configuration=");
		builder.append(configuration);
		builder.append(", artifact=");
		builder.append(artifact);
		builder.append(", ext=");
		builder.append(ext);
		builder.append(", classifier=");
		builder.append(classifier);
		builder.append(", parentArtifact=");
		builder.append(parentArtifact);
		builder.append("]");
		return builder.toString();
	}
	
}
