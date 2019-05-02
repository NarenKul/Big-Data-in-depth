if [ -z "${SPARK_HOME}" ]; then
  source "$(dirname "$0")"/find-spark-home
fi

source "${SPARK_HOME}"/bin/load-spark-env.sh
export _SPARK_CMD_USAGE="Usage: ./bin/pyspark [options]"

# In Spark 2.0, IPYTHON and IPYTHON_OPTS are removed and pyspark fails to launch if either option
# is set in the user's environment. Instead, users should set PYSPARK_DRIVER_PYTHON=ipython
# to use IPython and set PYSPARK_DRIVER_PYTHON_OPTS to pass options when starting the Python driver
# (e.g. PYSPARK_DRIVER_PYTHON_OPTS='notebook').  This supports full customization of the IPython
# and executor Python executables.

# Fail noisily if removed options are set
if [[ -n "$IPYTHON" || -n "$IPYTHON_OPTS" ]]; then
  echo "Error in pyspark startup:"
  echo "IPYTHON and IPYTHON_OPTS are removed in Spark 2.0+. Remove these from the environment and set PYSPARK_DRIVER_PYTHON and PYSPARK_DRIVER_PYTHON_OPTS instead."
  exit 1
fi

# Default to standard python interpreter unless told otherwise
if [[ -z "$PYSPARK_PYTHON" ]]; then
  PYSPARK_PYTHON=python
fi
if [[ -z "$PYSPARK_DRIVER_PYTHON" ]]; then
  PYSPARK_DRIVER_PYTHON=$PYSPARK_PYTHON
fi
export PYSPARK_PYTHON
export PYSPARK_DRIVER_PYTHON
export PYSPARK_DRIVER_PYTHON_OPTS

# Add the PySpark classes to the Python path:
export PYTHONPATH="${SPARK_HOME}/python/:$PYTHONPATH"
export PYTHONPATH="${SPARK_HOME}/python/lib/py4j-0.10.8.1-src.zip:$PYTHONPATH"

# Load the PySpark shell.py script when ./pyspark is used interactively:
export OLD_PYTHONSTARTUP="$PYTHONSTARTUP"
export PYTHONSTARTUP="${SPARK_HOME}/python/pyspark/shell.py"

# For pyspark tests
if [[ -n "$SPARK_TESTING" ]]; then
  unset YARN_CONF_DIR
  unset HADOOP_CONF_DIR
  export PYTHONHASHSEED=0
  exec "$PYSPARK_DRIVER_PYTHON" -m "$@"
  exit
fi

exec "${SPARK_HOME}"/bin/spark-submit pyspark-shell-main --name "PySparkShell" "$@"
