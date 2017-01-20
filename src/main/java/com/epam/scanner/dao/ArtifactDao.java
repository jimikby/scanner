package com.epam.scanner.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.epam.scanner.dao.rowmapper.ArtifactRowMapper;
import com.epam.scanner.domain.Artifact;

@Repository
@Transactional
public class ArtifactDao {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<Artifact> getAll() {
		return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts", new ArtifactRowMapper());
	}
	
	public void saveAll(List<Artifact> artifacts, String type) {
		List<Artifact> list = new ArrayList<>(artifacts);
		jdbcTemplate.batchUpdate("INSERT INTO artifacts (artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension, artifact_type) VALUES (?,?,?,?,?,?,?,?,?,?)", new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Artifact artifact = list.get(i);
		
					if (artifact.getExclude()== null) {artifact.setExclude(false);}
				ps.setString(1, artifact.getArtifactId());
				ps.setString(2, artifact.getGroupId());
				ps.setString(3, artifact.getVersion());
				ps.setString(4, artifact.getHardcodedVersion());
				ps.setString(5, artifact.getExclude().toString());
				ps.setString(6, artifact.getPath());
				ps.setString(7, artifact.getName());
				ps.setString(8, artifact.getClassifier());
				ps.setString(9, artifact.getExtension());
				ps.setString(10, type);
			}
			@Override
			public int getBatchSize() {
				return artifacts.size();
			}
		});
	}

	public List<Artifact> takeByGroupId(String groupId) {
			try {
				return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts WHERE group_id = ?", new Object[] { groupId },
						new ArtifactRowMapper());
			} catch (
			EmptyResultDataAccessException e) {
				return null;
			}
		}

	public List<Artifact> takeByArtifactId(String artifactId) {
		try {
			return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts WHERE artifact_id = ?", new Object[] { artifactId },
					new ArtifactRowMapper());
		} catch (
		EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	public List<Artifact> takeByVersion(String version) {
		try {
			return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts WHERE version = ?", new Object[] { version },
					new ArtifactRowMapper());
		} catch (
		EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Artifact> takeByPath(String path) {
		try {
			return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts WHERE path = ?", new Object[] { path },
					new ArtifactRowMapper());
		} catch (
		EmptyResultDataAccessException e) {
			return null;
		}
	}

	public List<Artifact> takeByNameAndHadcodedVersion(String value,String hardcodedVersion) {
		try {
			return jdbcTemplate.query("SELECT artifact_id, group_id, version, hardcoded_version, exclude, path, name, classifier, extension,artifact_type FROM artifacts WHERE artifact_id  = ? AND hardcoded_version = ?", new Object[] {value, hardcodedVersion},
					new ArtifactRowMapper());
		} catch (
		EmptyResultDataAccessException e) {
			return null;
		}
	}
}

