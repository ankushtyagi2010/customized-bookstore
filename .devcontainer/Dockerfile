# Development container for Customized Bookstore
FROM mcr.microsoft.com/devcontainers/java:17

# Install additional tools
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends \
    git \
    curl \
    wget \
    vim \
    net-tools \
    iputils-ping \
    && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Install Maven (specific version to match project)
ARG MAVEN_VERSION=3.9.6
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Set up workspace
RUN mkdir -p /workspace /app/uploads/characters /app/uploads/covers /app/uploads/user-uploads /app/uploads/pdfs

# Configure user permissions
RUN chown -R vscode:vscode /workspace /app

# Switch to vscode user
USER vscode

# Set working directory
WORKDIR /workspace

# Set environment variables
ENV MAVEN_HOME=/usr/share/maven
ENV M2_HOME=/usr/share/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"
ENV JAVA_HOME=/opt/java/openjdk
