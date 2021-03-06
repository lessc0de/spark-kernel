/*
 * Copyright 2014 IBM Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
logLevel := Level.Warn

resolvers += Classpaths.sbtPluginReleases

// Provides the ability to create an IntelliJ project using `sbt gen-idea`
addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.6.0")

// Provides the ability to generate unifed documentation for multiple projects
addSbtPlugin("com.eed3si9n" % "sbt-unidoc" % "0.3.1")

// Provides the ability to list dependencies in a readable format using
// `sbt dependencyTree`; there are other commands provided as well
addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.7.4")

// Provides ability to view code coverage via `sbt scoverage:test`
addSbtPlugin("org.scoverage" %% "sbt-scoverage" % "0.99.5.1")

// Provides ability to create a pack containing all jars and a script to run them
// using `sbt pack` or `sbt pack-archive` to generate a *.tar.gz file
addSbtPlugin("org.xerial.sbt" % "sbt-pack" % "0.6.1")

//  Provides the ability to package our project as a docker image
addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "0.5.2")

addSbtPlugin("com.eed3si9n" % "sbt-buildinfo" % "0.3.2")