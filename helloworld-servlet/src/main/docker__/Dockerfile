FROM gcr.io/google_appengine/jetty9
ADD helloworld-servlet-1.0-SNAPSHOT.war $JETTY_BASE/webapps/root.war

RUN rm -rf /opt/cprof && mkdir /opt/cprof && cd /opt/cprof && \
    wget -qO- https://storage.googleapis.com/cloud-profiler/appengine-java/flex/profiler_java_agent.tar.gz | tar xzv
RUN ls /opt/cprof

ENV CPROF_OPTS --cprof_force=cpu
ENV CPROF_OPTS $CPROF_OPTS,--cprof_delay_sec=20
ENV CPROF_OPTS $CPROF_OPTS,--cprof_interval_sec=1800
ENV CPROF_OPTS $CPROF_OPTS,--cprof_duration_sec=30
ENV CPROF_OPTS $CPROF_OPTS,--cprof_max_count=200
ENV JAVA_OPTS -agentpath:/opt/cprof/profiler_java_agent.so=$CPROF_OPTS
