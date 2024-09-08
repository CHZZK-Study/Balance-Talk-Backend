package balancetalk.global.config;

import org.hibernate.boot.model.FunctionContributions;
import org.hibernate.boot.model.FunctionContributor;
import org.hibernate.type.BasicType;
import org.hibernate.type.StandardBasicTypes;

public class MySQLFunctionContributor implements FunctionContributor {

    @Override
    public void contributeFunctions(FunctionContributions functionContributions) {
        functionContributions.getFunctionRegistry()
                .registerPattern(
                        "match_talk_pick_in_boolean_mode",
                        "MATCH(?1, ?2, ?3, ?4, ?5, ?6, ?7) AGAINST(?8 IN BOOLEAN MODE)",
                        getBooleanBasicType(functionContributions));

        functionContributions.getFunctionRegistry()
                .registerPattern(
                        "match_talk_pick_in_natural_mode",
                        "MATCH(?1, ?2, ?3, ?4, ?5, ?6, ?7) AGAINST(?8)",
                        getBooleanBasicType(functionContributions));
    }

    private BasicType<Boolean> getBooleanBasicType(FunctionContributions functionContributions) {
        return functionContributions.getTypeConfiguration()
                .getBasicTypeRegistry()
                .resolve(StandardBasicTypes.BOOLEAN);
    }
}
