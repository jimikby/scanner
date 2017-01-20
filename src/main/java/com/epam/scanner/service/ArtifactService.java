package com.epam.scanner.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epam.scanner.dao.ArtifactDao;
import com.epam.scanner.domain.Artifact;

@Service
public class ArtifactService {
	
		@Autowired
		private ArtifactDao artifactDao;
		
		public List<Artifact> takeByGroupId(String groupId) {
			return artifactDao.takeByGroupId(groupId);
			
		}
		
		public List<Artifact> takeByArtifactId(String artifactId) {
			return artifactDao.takeByArtifactId(artifactId);
			
		}
	
		
		public List<Artifact> getAll() {
			return artifactDao.getAll();
		}

		public List<Artifact> takeByVersion(String value) {
			return artifactDao.takeByVersion(value);
		}

		public void saveAll(List<Artifact> artifacts, String type) {
			System.out.println(artifacts);
			artifactDao.saveAll(artifacts,type);
		}

		public List<Artifact> takeByPath(String value) {
			return artifactDao.takeByPath(value);

		}
		
		public List<Artifact> takeByNameAndHadcodedVersion(String value,String hardcodedVersion ) {
			return artifactDao.takeByNameAndHadcodedVersion(value, hardcodedVersion);

		}
		

	}
