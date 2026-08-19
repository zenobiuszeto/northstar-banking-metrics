export function BarChart({values, labels, comparison = []}) {
  return (
    <div className="bars" role="img" aria-label="Actual compared with plan or prior period">
      {values.map((value, index) => (
        <div className="barWrap" key={labels[index]}><div className="barPair">
          <i style={{height: value}} />
          {comparison[index] != null && <i className="compare" style={{height: comparison[index]}} />}
        </div><small>{labels[index]}</small></div>
      ))}
    </div>
  );
}
