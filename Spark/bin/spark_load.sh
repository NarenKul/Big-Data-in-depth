# Figure out where Spark is installed
if [ -z "${SPARK_HOME}" ]; then
  source "$(dirname "$0")"/find-spark-home
fi

SPARK_ENV_SH="spark-env.sh"
if [ -z "$SPARK_ENV_LOADED" ]; then
  export SPARK_ENV_LOADED=1

  export SPARK_CONF_DIR="${SPARK_CONF_DIR:-"${SPARK_HOME}"/conf}"

  SPARK_ENV_SH="${SPARK_CONF_DIR}/${SPARK_ENV_SH}"
  if [[ -f "${SPARK_ENV_SH}" ]]; then
    # Promote all variable declarations to environment (exported) variables
    set -a
    . ${SPARK_ENV_SH}
    set +a
  fi
fi

# Setting SPARK_SCALA_VERSION if not already set.

# TODO: revisit for Scala 2.13 support
export SPARK_SCALA_VERSION=2.12
#if [ -z "$SPARK_SCALA_VERSION" ]; then
#  SCALA_VERSION_1=2.12
#  SCALA_VERSION_2=2.11
#
#  ASSEMBLY_DIR_1="${SPARK_HOME}/assembly/target/scala-${SCALA_VERSION_1}"
#  ASSEMBLY_DIR_2="${SPARK_HOME}/assembly/target/scala-${SCALA_VERSION_2}"
#  ENV_VARIABLE_DOC="https://spark.apache.org/docs/latest/configuration.html#environment-variables"
#  if [[ -d "$ASSEMBLY_DIR_1" && -d "$ASSEMBLY_DIR_2" ]]; then
#    echo "Presence of build for multiple Scala versions detected ($ASSEMBLY_DIR_1 and $ASSEMBLY_DIR_2)." 1>&2
#    echo "Remove one of them or, export SPARK_SCALA_VERSION=$SCALA_VERSION_1 in ${SPARK_ENV_SH}." 1>&2
#    echo "Visit ${ENV_VARIABLE_DOC} for more details about setting environment variables in spark-env.sh." 1>&2
#    exit 1
#  fi
#
#  if [[ -d "$ASSEMBLY_DIR_1" ]]; then
#    export SPARK_SCALA_VERSION=${SCALA_VERSION_1}
#  else
#    export SPARK_SCALA_VERSION=${SCALA_VERSION_2}
#  fi
#fi
