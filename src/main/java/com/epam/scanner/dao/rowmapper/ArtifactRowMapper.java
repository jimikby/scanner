package com.epam.scanner.dao.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.epam.scanner.domain.Artifact;

public class ArtifactRowMapper implements RowMapper<Artifact> {

	@Override
	public Artifact mapRow(ResultSet rs, int rowNum) throws SQLException {
		Artifact artifact = new Artifact();
		artifact.setArtifactId(rs.getString("artifact_id"));
		artifact.setGroupId(rs.getString("group_id"));
		artifact.setVersion(rs.getString("version"));
		artifact.setExclude(rs.getBoolean("exclude"));
		artifact.setPath(rs.getString("path"));
		artifact.setName(rs.getString("name"));
		artifact.setClassifier(rs.getString("classifier"));
		artifact.setExtension(rs.getString("extension"));
		artifact.setType(rs.getString("artifact_type"));
		return artifact;
	}

}
