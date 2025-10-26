package kitva.build

import org.gradle.api.Plugin
import org.gradle.api.Project

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class BuildLogger implements Plugin<Project> {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

  private Project project
  private Level logLevel;

  @Override
  void apply(Project project) {
    this.project = project
    Level level = Level.from(project.findProperty("buildLogLevel").toString())
    this.logLevel = level == null ? Level.INFO : level
    //project.extensions.add("logger", this)
  }

  void trace(Object... msg) {
    log(Level.TRACE, msg)
  }

  void debug(Object... msg) {
    log(Level.DEBUG, msg)
  }

  void info(Object... msg) {
    log(Level.INFO, msg)
  }

  void warn(Object... msg) {
    log(Level.WARN, msg)
  }

  void error(Object... msg) {
    log(Level.ERROR, msg)
  }

  private void log(Level level, Object... msg) {
    if (logLevel.value >= level.value) {
      return;
    }
    String message = "[${FORMATTER.format(LocalDateTime.now())}][${level}][$project.name]: ${msg.join("")}"
    println(message)
  }

  private static final enum Level {

    TRACE(0),
    DEBUG(1),
    INFO(2),
    WARN(3),
    ERROR(4),
    ;

    private final int value;

    private Level(int value) {
      this.value = value;
    }

    static Level from(String level) {
      try {
        return valueOf(level.toUpperCase())
      } catch (Exception ignored) {
        return null;
      }
    }
  }
}

