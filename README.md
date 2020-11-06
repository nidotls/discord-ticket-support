<div align="center">

![Logo](logo.png)

# IWTemplate

[![Discord](https://img.shields.io/discord/617339081168388110?color=green&label=discord&logo=discord&logoColor=white&style=for-the-badge)](https://discord.gg/mEnDydK)
</div>

## Maven Repo

Link: https://repo.iwmedia.dev/

- `MAVEN_REPO_USER`
- `MAVEN_REPO_PASS`

## GitFlow

Uses: [gitflow-maven-plugin](https://github.com/aleksandr-m/gitflow-maven-plugin)

- `gitflow:release-start` - Starts a release branch and updates version(s) to release version.
- `gitflow:release-finish` - Merges a release branch and updates version(s) to next development version.
- `gitflow:release` - Releases project w/o creating a release branch.
- `gitflow:feature-start` - Starts a feature branch and optionally updates version(s).
- `gitflow:feature-finish` - Merges a feature branch.
- `gitflow:hotfix-start` - Starts a hotfix branch and updates version(s) to hotfix version.
- `gitflow:hotfix-finish` - Merges a hotfix branch.
- `gitflow:support-start` - Starts a support branch from the production tag.
- `gitflow:help` - Displays help information.
