# SE Commons Library

This is a common utilities library for Spring Boot applications.

## Publishing

This library is published to GitHub Packages. The GitHub Actions workflow automatically publishes new versions of the library when:

1. Changes are pushed to the main/master branch (as SNAPSHOT version)
2. A new tag with format `v*` is pushed (as release version)
3. Manual trigger through GitHub Actions UI

The workflow uses the `SEBACKEND_ACCESS_TOKEN` GitHub secret for authentication. Make sure this token has the required permissions: `read:packages` and `write:packages`.

## Using this library in your projects

### 1. Add GitHub Packages as a Maven repository

In your project's `pom.xml` file, add the GitHub Packages repository:

```xml
<repositories>
    <repository>
        <id>github</id>
        <name>GitHub Packages</name>
        <url>https://maven.pkg.github.com/OWNER/REPOSITORY</url>
    </repository>
</repositories>
```

Replace `OWNER/REPOSITORY` with the GitHub username and repository name.

### 2. Configure authentication

Create or edit the `~/.m2/settings.xml` file:

```xml
<settings>
  <servers>
    <server>
      <id>github</id>
      <username>YOUR_GITHUB_USERNAME</username>
      <password>YOUR_GITHUB_TOKEN</password>
    </server>
  </servers>
</settings>
```

Note: The GitHub token needs to have `read:packages` permission.

For CI/CD environments, you can use the GitHub secret `SEBACKEND_ACCESS_TOKEN` in your workflows.

### 3. Add the dependency to your project

```xml
<dependency>
    <groupId>com.sorted</groupId>
    <artifactId>se-commons</artifactId>
    <version>VERSION</version>
</dependency>
```

Replace `VERSION` with the version you want to use.

## Releasing a new version

To release a new version:

1. Push changes to the main branch for SNAPSHOT updates
2. Create and push a new tag to release a specific version:

```bash
git tag v1.0.0
git push origin v1.0.0
```

This will trigger the GitHub Actions workflow to publish version 1.0.0 of the library. 