# Development container for Customized Bookstore
FROM mcr.microsoft.com/devcontainers/java:17

ARG TZ
ENV TZ="$TZ"

ARG CLAUDE_CODE_VERSION=latest

# Install Node.js and additional tools
RUN apt-get update && apt-get install -y --no-install-recommends \
  curl \
  ca-certificates \
  gnupg \
  && mkdir -p /etc/apt/keyrings \
  && curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg \
  && echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_20.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list \
  && apt-get update && apt-get install -y --no-install-recommends \
  nodejs \
  less \
  git \
  procps \
  sudo \
  fzf \
  zsh \
  man-db \
  unzip \
  gnupg2 \
  gh \
  iptables \
  ipset \
  iproute2 \
  dnsutils \
  aggregate \
  jq \
  nano \
  vim \
  && apt-get clean && rm -rf /var/lib/apt/lists/*

# Install MongoDB 7.0 (for local development without separate container)
RUN curl -fsSL https://www.mongodb.org/static/pgp/server-7.0.asc | gpg --dearmor -o /usr/share/keyrings/mongodb-server-7.0.gpg \
  && echo "deb [ signed-by=/usr/share/keyrings/mongodb-server-7.0.gpg ] http://repo.mongodb.org/apt/debian bookworm/mongodb-org/7.0 main" | tee /etc/apt/sources.list.d/mongodb-org-7.0.list \
  && apt-get update && apt-get install -y --no-install-recommends \
  mongodb-org \
  mongodb-mongosh \
  && apt-get clean && rm -rf /var/lib/apt/lists/* \
  && mkdir -p /data/db /var/log/mongodb \
  && chown -R mongodb:mongodb /data/db /var/log/mongodb

ARG USERNAME=node
ARG USER_UID=1000
ARG USER_GID=$USER_UID

# Create or rename user to node
RUN set -e; \
  EXISTING_USER=$(getent passwd $USER_UID | cut -d: -f1 || echo ""); \
  EXISTING_GROUP=$(getent group $USER_GID | cut -d: -f1 || echo ""); \
  if [ -n "$EXISTING_USER" ] && [ "$EXISTING_USER" != "$USERNAME" ]; then \
    echo "Renaming existing user $EXISTING_USER (UID $USER_UID) to $USERNAME"; \
    usermod -l $USERNAME -d /home/$USERNAME -m $EXISTING_USER || true; \
    if [ -n "$EXISTING_GROUP" ] && [ "$EXISTING_GROUP" != "$USERNAME" ]; then \
      groupmod -n $USERNAME $EXISTING_GROUP 2>/dev/null || true; \
    fi; \
  elif [ -z "$EXISTING_USER" ]; then \
    echo "Creating new user $USERNAME with UID $USER_UID"; \
    if [ -z "$EXISTING_GROUP" ]; then \
      groupadd --gid $USER_GID $USERNAME; \
    fi; \
    useradd --uid $USER_UID --gid $USER_GID -m -s /bin/bash $USERNAME; \
  else \
    echo "User $USERNAME already exists with UID $USER_UID"; \
  fi; \
  echo "$USERNAME ALL=(root) NOPASSWD:ALL" > /etc/sudoers.d/$USERNAME; \
  chmod 0440 /etc/sudoers.d/$USERNAME

# Ensure node user has access to /usr/local/share
# The base image has vscode user, but we'll use node user instead
RUN mkdir -p /usr/local/share/npm-global && \
  chown -R node:node /usr/local/share

# Persist bash history.
RUN SNIPPET="export PROMPT_COMMAND='history -a' && export HISTFILE=/commandhistory/.bash_history" \
  && mkdir /commandhistory \
  && touch /commandhistory/.bash_history \
  && chown -R $USERNAME /commandhistory

# Set `DEVCONTAINER` environment variable to help with orientation
ENV DEVCONTAINER=true

# Create workspace, config, and maven directories and set permissions
RUN mkdir -p /workspace /home/node/.claude /home/node/.m2/repository && \
  chown -R node:node /workspace /home/node/.claude /home/node/.m2

WORKDIR /workspace

ARG GIT_DELTA_VERSION=0.18.2
RUN ARCH=$(dpkg --print-architecture) && \
  wget "https://github.com/dandavison/delta/releases/download/${GIT_DELTA_VERSION}/git-delta_${GIT_DELTA_VERSION}_${ARCH}.deb" && \
  dpkg -i "git-delta_${GIT_DELTA_VERSION}_${ARCH}.deb" && \
  rm "git-delta_${GIT_DELTA_VERSION}_${ARCH}.deb"

# Install Maven (specific version to match project) - needs to run as root
ARG MAVEN_VERSION=3.9.6
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz | tar xzf - -C /usr/share \
    && mv /usr/share/apache-maven-${MAVEN_VERSION} /usr/share/maven \
    && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn

# Create app directories
RUN mkdir -p /app/uploads/characters /app/uploads/covers /app/uploads/user-uploads /app/uploads/pdfs && \
  chown -R node:node /app

# Copy initialization and helper scripts
COPY .devcontainer/start-mongodb.sh /usr/local/bin/
COPY .devcontainer/setup-env.sh /usr/local/bin/
COPY .devcontainer/run-app.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/start-mongodb.sh /usr/local/bin/setup-env.sh /usr/local/bin/run-app.sh

# Temporarily disabled - debugging COPY issue
# COPY .devcontainer/init-firewall.sh /usr/local/bin/
# RUN chmod +x /usr/local/bin/init-firewall.sh && \
#   echo "node ALL=(root) NOPASSWD: /usr/local/bin/init-firewall.sh" > /etc/sudoers.d/node-firewall && \
#   chmod 0440 /etc/sudoers.d/node-firewall

# Allow node user to manage MongoDB
RUN echo "node ALL=(root) NOPASSWD: /usr/bin/service mongod *, /usr/bin/mkdir *, /usr/bin/chown *, /usr/sbin/service mongod *" > /etc/sudoers.d/node-mongodb && \
  echo "node ALL=(mongodb) NOPASSWD: /usr/bin/mongod" >> /etc/sudoers.d/node-mongodb && \
  chmod 0440 /etc/sudoers.d/node-mongodb

# Set environment variables
ENV MAVEN_HOME=/usr/share/maven
ENV M2_HOME=/usr/share/maven
ENV PATH="${MAVEN_HOME}/bin:${PATH}"
ENV JAVA_HOME=/opt/java/openjdk

# Set up non-root user
USER node

# Install global packages
ENV NPM_CONFIG_PREFIX=/usr/local/share/npm-global
ENV PATH=$PATH:/usr/local/share/npm-global/bin

# Set the default shell to zsh rather than sh
ENV SHELL=/bin/zsh

# Set the default editor and visual
ENV EDITOR=nano
ENV VISUAL=nano

# Default powerline10k theme
ARG ZSH_IN_DOCKER_VERSION=1.2.0
RUN sh -c "$(wget -O- https://github.com/deluan/zsh-in-docker/releases/download/v${ZSH_IN_DOCKER_VERSION}/zsh-in-docker.sh)" -- \
  -p git \
  -p fzf \
  -a "source /usr/share/doc/fzf/examples/key-bindings.zsh" \
  -a "source /usr/share/doc/fzf/examples/completion.zsh" \
  -a "export PROMPT_COMMAND='history -a' && export HISTFILE=/commandhistory/.bash_history" \
  -a "[ -f /usr/local/bin/setup-env.sh ] && source /usr/local/bin/setup-env.sh" \
  -x

# Also add to bashrc for bash users
RUN echo '[ -f /usr/local/bin/setup-env.sh ] && source /usr/local/bin/setup-env.sh' >> /home/node/.bashrc && \
  echo 'alias run-app="/usr/local/bin/run-app.sh"' >> /home/node/.bashrc && \
  echo 'alias mongo-start="/usr/local/bin/start-mongodb.sh"' >> /home/node/.bashrc && \
  echo 'alias mongo-check="mongosh --quiet --eval \"db.adminCommand(\\\"ping\\\")\" && echo \"MongoDB is running\""' >> /home/node/.bashrc

# Add aliases to zshrc
RUN echo 'alias run-app="/usr/local/bin/run-app.sh"' >> /home/node/.zshrc && \
  echo 'alias mongo-start="/usr/local/bin/start-mongodb.sh"' >> /home/node/.zshrc && \
  echo 'alias mongo-check="mongosh --quiet --eval \"db.adminCommand(\\\"ping\\\")\" && echo \"MongoDB is running\""' >> /home/node/.zshrc

# Install Claude Code as node user
RUN npm install -g @anthropic-ai/claude-code@${CLAUDE_CODE_VERSION}
