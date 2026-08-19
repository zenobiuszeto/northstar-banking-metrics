export function ProductNav({products, selected, onSelect}) {
  return (
    <nav aria-label="Banking products">
      {products.map((product) => (
        <button className={product === selected ? 'active' : ''} type="button"
          aria-current={product === selected ? 'page' : undefined}
          onClick={() => onSelect(product)} key={product}>{product}</button>
      ))}
    </nav>
  );
}
