import java.util.ArrayList;
import java.util.List;

public class AstToSource implements Expr.Visitor<String> {
    String transform(Expr ast) {
        String result = ast.accept(this);
        result += result.equals("") ? "" : ";";
        return result;
    }
    public void transform(BasicBlock block) {
        List<String> strings = new ArrayList<>();
        for (Object expr : block.getInstructions()) {
            Expr expression = (Expr) expr;
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
