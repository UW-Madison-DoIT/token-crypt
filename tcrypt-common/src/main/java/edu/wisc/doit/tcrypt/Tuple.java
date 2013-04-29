package edu.wisc.doit.tcrypt;

final class Tuple<V1, V2> {
    final V1 v1;
    final V2 v2;

    public Tuple(V1 v1, V2 v2) {
        this.v1 = v1;
        this.v2 = v2;
    }

    public V1 getV1() {
        return v1;
    }

    public V2 getV2() {
        return v2;
    }
}