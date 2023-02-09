package semanticAnalysis.types;

import semanticAnalysis.IScope;
import semanticAnalysis.ScopedSymbol;

public class AggregateType extends ScopedSymbol implements IType {

    protected Class<?> originType;

    /**
     * Constructor
     *
     * @param name Name of this type
     * @param parentScope parent scope of this type
     */
    public AggregateType(String name, IScope parentScope) {
        super(name, parentScope, null);
        originType = null;
    }

    /**
     * Constructor
     *
     * @param name Name of this type
     * @param parentScope parent scope of this type
     * @param originType the origin java class of this {@link AggregateType}, if it was generated by
     *     the {@link TypeBuilder}
     */
    public AggregateType(String name, IScope parentScope, Class<?> originType) {
        super(name, parentScope, null);
        this.originType = originType;
    }

    @Override
    public Kind getTypeKind() {
        return Kind.Aggregate;
    }

    /**
     * @return the origin java class (or null, if it was not set)
     */
    public Class<?> getOriginType() {
        return this.originType;
    }
}