/*
 * Copyright 2015 Red Hat Inc. and/or its affiliates and other contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.qa.jenkins.test.executor;

import org.jboss.qa.jenkins.test.executor.beans.Workspace;
import org.jboss.qa.jenkins.test.executor.phase.cleanup.CleanUpPhase;
import org.jboss.qa.jenkins.test.executor.phase.download.DownloadPhase;
import org.jboss.qa.jenkins.test.executor.phase.maven.MavenPhase;
import org.jboss.qa.jenkins.test.executor.phase.runtimeconfiguration.RuntimeConfigurationPhase;
import org.jboss.qa.jenkins.test.executor.phase.start.StartPhase;
import org.jboss.qa.jenkins.test.executor.phase.staticconfiguration.StaticConfigurationPhase;
import org.jboss.qa.jenkins.test.executor.phase.stop.StopPhase;
import org.jboss.qa.jenkins.test.executor.utils.JenkinsUtils;
import org.jboss.qa.phaser.InstanceRegistry;
import org.jboss.qa.phaser.PhaseTreeBuilder;
import org.jboss.qa.phaser.Phaser;

import java.io.File;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JenkinsTestExecutor {

	public static final File WORKSPACE = new File(JenkinsUtils.getUniversalProperty("workspace", "target"));

	private Class<?> jobClass;

	public JenkinsTestExecutor(Class<?> jobClass) {
		this.jobClass = jobClass;
	}

	public void run() throws Exception {
		// Set default workspace
		InstanceRegistry.insert(new Workspace(WORKSPACE));

		// Create phase-tree
		final PhaseTreeBuilder builder = new PhaseTreeBuilder();
		builder
				.addPhase(new DownloadPhase())
				.next()
				.addPhase(new StaticConfigurationPhase())
				.addPhase(new StartPhase())
				.addPhase(new RuntimeConfigurationPhase())
				.addPhase(new MavenPhase())
				.addPhase(new StopPhase())
				.addPhase(new CleanUpPhase());

		// Run the Phaser
		new Phaser(builder.build(), jobClass).run();
	}
}
