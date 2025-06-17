package simulation;

import core.dynamo.DynamoBase;

public final class DynamoPerformer {
    private DynamoBase dBase;

    public DynamoPerformer(int n, int w, int r, double q) {
        dBase = new DynamoBase(n, w, r, q);
    }

    public void simulate(int numSlots) {
        for (int i = 0; i < numSlots; i++) {
            dBase.doWrite();
        }
        System.out.println("doRead(): " + dBase.doRead());
    }

    @Override
    public String toString() {
        return dBase.toString();
    }

    // конструктор через Builder
    private DynamoPerformer(Builder builder) {
        dBase = new DynamoBase(builder.n,
                               builder.w,
                               builder.r,
                               builder.q);
    }

    // паттерн Builder для удобного заполнения множества аргументов
    public static class Builder {
        private int n;
        private int w;
        private int r;
        private double q;

        public Builder setN(int n) {
            this.n = n;
            return this;
        }

        public Builder setW(int w) {
            this.w = w;
            return this;
        }

        public Builder setR(int r) {
            this.r = r;
            return this;
        }

        public Builder setQ(double q) {
            this.q = q;
            return this;
        }

        public DynamoPerformer build() {
            return new DynamoPerformer(this);
        }
    }
}
