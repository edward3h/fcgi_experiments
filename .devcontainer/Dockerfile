# See here for image contents: https://github.com/microsoft/vscode-dev-containers/tree/v0.134.1/containers/ubuntu/.devcontainer/base.Dockerfile
ARG VARIANT="bionic"
FROM mcr.microsoft.com/vscode/devcontainers/base:0-${VARIANT}

# [Optional] Uncomment this section to install additional OS packages.
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends bash-completion build-essential tree ack-grep vim libfcgi-perl zlib1g-dev libfcgi-dev

ENV SDKMAN_DIR=/root/.sdkman
RUN  curl -s "https://get.sdkman.io" | bash && \
    echo "sdkman_auto_answer=true" > $SDKMAN_DIR/etc/config && \
    echo "sdkman_auto_selfupdate=false" >> $SDKMAN_DIR/etc/config && \
    echo "sdkman_insecure_ssl=true" >> $SDKMAN_DIR/etc/config && \
    bash -c ". /root/.sdkman/bin/sdkman-init.sh && sdk install java 20.2.0.r11-grl" 
ENV JAVA_HOME=${SDKMAN_DIR}/candidates/java/current
RUN curl -LA gradle-completion https://edub.me/gradle-completion-bash -o /etc/bash_completion.d/gradle-completion.bash
RUN git clone https://github.com/dougborg/gdub.git && \
    cd gdub && \
    ./install

# Install Docker CE CLI
RUN apt-get update \
    && apt-get install -y apt-transport-https ca-certificates curl gnupg2 lsb-release \
    && curl -fsSL https://download.docker.com/linux/$(lsb_release -is | tr '[:upper:]' '[:lower:]')/gpg | apt-key add - 2>/dev/null \
    && echo "deb [arch=amd64] https://download.docker.com/linux/$(lsb_release -is | tr '[:upper:]' '[:lower:]') $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list \
    && apt-get update \
    && apt-get install -y docker-ce-cli

# Install Docker Compose
RUN LATEST_COMPOSE_VERSION=$(curl -sSL "https://api.github.com/repos/docker/compose/releases/latest" | grep -o -P '(?<="tag_name": ").+(?=")') \
    && curl -sSL "https://github.com/docker/compose/releases/download/${LATEST_COMPOSE_VERSION}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose \
    && chmod +x /usr/local/bin/docker-compose
    
RUN ${JAVA_HOME}/bin/gu install native-image