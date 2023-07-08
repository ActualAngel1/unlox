import java.util.List;

abstract class Expr {
    interface Visitor<R> {
        R visitAssignExpr(Assign expr);
        R visitCallExpr(Call expr);
        R visitReturnExpr(Return stmt);
        R visitPrintExpr(Print expr);
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);
    }
    static class Call extends Expr {
        Call(Expr callee, Instruction instruction, List<Expr> arguments) {
            this.callee = callee;
            this.instruction = instruction;
            this.arguments = arguments;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitCallExpr(this);
        }

        final Expr callee;
        final Instruction instruction;
        final List<Expr> arguments;
    }
    static class Assign extends Expr {
        Assign(Expr name, Expr value) {
            this.name = name;
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitAssignExpr(this);
        }

        final Expr name;
        final Expr value;
    }
    static class Return extends Expr {
        Return(Expr value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitReturnExpr(this);
        }

        final Expr value;
    }
    static class Print extends Expr {
        Print(Expr inner) {
            this.inner = inner;
        }

        @Override
        <R> R accept(Visitor<R> visitor) { return visitor.visitPrintExpr(this); }

        final Expr inner;
    }
    static class Binary extends Expr {
        Binary(Expr left, Instruction operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }

        final Expr left;
        final Instruction operator;
        final Expr right;
    }

    static class Grouping extends Expr {
        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }

        final Expr expression;
    }

    static class Literal extends Expr {
        Literal(String value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }

        final String value;
    }

    abstract <R> R accept(Visitor<R> visitor);

    static class Unary extends Expr {
        Unary(Instruction operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }

        final Instruction operator;
        final Expr right;
    }
}
