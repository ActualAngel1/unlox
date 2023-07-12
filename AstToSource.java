import java.util.ArrayList;
import java.util.List;

public class AstToSource implements Expr.Visitor<String>, Stmt.Visitor<String> {
    String transform(Stmt ast) {
        return ast.accept(this);
    }
    public void transform(BasicBlock block) {
        List<String> strings = new ArrayList<>();
        for (Object expr : block.getInstructions()) {
            Stmt expression = (Stmt) expr;
            String source = transform(expression);
            strings.add(source);
        }

        block.setInstructions(new ArrayList<>(strings));
    }

    public void transformAll(List<BasicBlock> blocks) {
        for (BasicBlock block : blocks ) {
            transform(block);
        }
    }

    public String visitCallExpr(Expr.Call expr) {
        StringBuilder call = new StringBuilder(expr.callee.accept(this) + "(");
        int argCount = expr.arguments.size();
        if (argCount > 0) {
            for (int i = 0; i < argCount - 1; i++) {
                call.append(expr.arguments.get(i).accept(this)).append(", ");
            }

            call.append(expr.arguments.get(argCount - 1).accept(this));
        }

        return call.append(")").toString();
    }

    @Override
    public String visitWhileStmt(Stmt.While stmt) {
        return "while (" + stmt.condition.accept(this) + ") {\n" + stmt.body.accept(this) + "}\n";
    }

    @Override
    public String visitExpressionStmt(Stmt.Expression stmt) {
        String result = stmt.expression.accept(this);
        result += result.equals("") ? "" : ";";
        result += "\n";

        return result;
    }

    @Override
    public String visitIfStmt(Stmt.If stmt) {
        String elsePart = stmt.elseBranch != null ? "else { \n" + stmt.elseBranch.accept(this) + "}" : "";
        return "if (" + stmt.condition.accept(this) + ") {" + stmt.thenBranch.accept(this) + "} " + elsePart + "\n";
    }

    @Override
    public String visitBlockStmt(Stmt.Block stmt) {
        StringBuilder str = new StringBuilder();
        for (Stmt statement : stmt.statements) {
            str.append("\t").append(statement.accept(this));
        }

        return str.toString();
    }

    @Override
    public String visitReturnExpr(Expr.Return expr) {
        String returned = expr.value.accept(this);
        if (returned.equals("nil")) return "";

        return "return " + returned;
    }

    @Override
    public String visitAssignExpr(Expr.Assign expr) {
        return "var " + expr.name.accept(this) + " = " + expr.value.accept(this);
    }

    @Override
    public String visitPrintExpr(Expr.Print expr) {
        return "print(" + expr.inner.accept(this) + ")";
    }

    @Override
    public String visitLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) return "null";
        return expr.value;
    }

    @Override
    public String visitUnaryExpr(Expr.Unary expr) {
        return expr.operator.literal + " (" + expr.right.accept(this) + ")";
    }

    @Override
    public String visitBinaryExpr(Expr.Binary expr) {
        return expr.left.accept(this) + " " + expr.operator.literal + " " + expr.right.accept(this);
    }

    @Override
    public String visitGroupingExpr(Expr.Grouping expr) {
        return "(" + expr.expression.accept(this) + ")";
    }
}
