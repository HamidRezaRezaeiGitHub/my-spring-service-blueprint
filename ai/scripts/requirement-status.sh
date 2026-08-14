requirement_status_category() {
  normalized=$(printf '%s' "$1" | tr '[:upper:]' '[:lower:]')

  case "$normalized" in
    *block*|*blocked*|*stuck*|*wait*|*waiting*|*hold*)
      printf 'blocked'
      return
      ;;
    *cancel*|*canceled*|*cancelled*|*abandon*|*dropped*)
      printf 'cancelled'
      return
      ;;
    *park*|*parked*|*pause*|*paused*|*defer*|*deferred*|*later*|*backlog*)
      printf 'parked'
      return
      ;;
    *active*|*progress*|*working*|*implementing*|*investigat*)
      printf 'active'
      return
      ;;
    *plan*|*planning*|*draft*|*todo*|*queued*)
      printf 'planning'
      return
      ;;
    *complete*|*completed*|*done*|*finish*|*finished*|*implemented*|*closed*|*ship*|*shipped*)
      printf 'done'
      return
      ;;
    *)
      printf 'unknown'
      return
      ;;
  esac
}
