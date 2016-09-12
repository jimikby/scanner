package com.epam.scanner.domain;

import com.epam.scanner.utils.parser.common.ParsedItem;

public class Artifact implements AbstractDomainEntity, ParsedItem {

	private Long Id;
	private String groupId;
	private String artifactId;
	private String version;
	private Boolean exclude;
	private String type;
	private String path;

	public Artifact() {
	}

	public Artifact(Long id, String groupId, String artifactId, String version, Boolean exclude, String type,
			String path) {
		super();
		Id = id;
		this.groupId = groupId;
		this.artifactId = artifactId;
		this.version = version;
		this.exclude = exclude;
		this.type = type;
		this.path = path;
	}

	public Long getId() {
		return Id;
	}

	public void setId(Long id) {
		Id = id;
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
		if (exclude ==null) {exclude = false;}
		return exclude;
	}

	public void setExclude(Boolean exclude) {
		this.exclude = exclude;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Id == null) ? 0 : Id.hashCode());
		result = prime * result + ((artifactId == null) ? 0 : artifactId.hashCode());
		result = prime * result + ((exclude == null) ? 0 : exclude.hashCode());
		result = prime * result + ((groupId == null) ? 0 : groupId.hashCode());
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
		if (Id == null) {
			if (other.Id != null)
				return false;
		} else if (!Id.equals(other.Id))
			return false;
		if (artifactId == null) {
			if (other.artifactId != null)
				return false;
		} else if (!artifactId.equals(other.artifactId))
			return false;
		if (exclude == null) {
			if (other.exclude != null)
				return false;
		} else if (!exclude.equals(other.exclude))
			return false;
		if (groupId == null) {
			if (other.groupId != null)
				return false;
		} else if (!groupId.equals(other.groupId))
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
		builder.append("Artifact [Id=");
		builder.append(Id);
		builder.append(", groupId=");
		builder.append(groupId);
		builder.append(", artifactId=");
		builder.append(artifactId);
		builder.append(", version=");
		builder.append(version);
		builder.append(", exclude=");
		builder.append(exclude);
		builder.append(", type=");
		builder.append(type);
		builder.append(", path=");
		builder.append(path);
		builder.append("]");
		return builder.toString();
	}
	
}
