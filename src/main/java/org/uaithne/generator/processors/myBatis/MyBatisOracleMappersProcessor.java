/*
 * Copyright 2012 and beyond, Juan Luis Paz
 *
 * This file is part of Uaithne.
 *
 * Uaithne is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Uaithne is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Uaithne. If not, see <http://www.gnu.org/licenses/>.
 */
package org.uaithne.generator.processors.myBatis;

import java.util.Set;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import org.uaithne.annotations.myBatis.MyBatisOracleMapper;
import org.uaithne.generator.commons.EntityInfo;
import org.uaithne.generator.commons.FieldInfo;

@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("org.uaithne.annotations.myBatis.MyBatisOracleMapper")
public class MyBatisOracleMappersProcessor extends MyBatisMappersProcessor {
    
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment re) {
        for (Element element : re.getElementsAnnotatedWith(MyBatisOracleMapper.class)) {
            if (element.getKind() == ElementKind.CLASS) {
                process(re, element);
            }
        }
        return true; // no further processing of this annotation type
    }
    
    @Override
    public String currentSqlDate() {
        return "sysdate";
    }
    
    @Override
    public String falseValue() {
        return "0";
    }
    
    @Override
    public String trueValue() {
        return "1";
    }
    
    @Override
    public boolean useAliasInOrderBy() {
        return false;
    }

    @Override
    public boolean insertQueryIncludeId() {
        return true;
    }
    
    @Override
    public String[] getDefaultIdNextValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"seq_" + getTableName(entity)[0] + ".nextval"};
    }
    
    @Override
    public String[] getDefaultIdCurrentValue(EntityInfo entity, FieldInfo field) {
        return new String[] {"select seq_" + getTableName(entity)[0] + ".currval from dual"};
    }

    @Override
    public String[] envolveInSelectPage(String[] query) {
        String[] r = new String[query.length + 2];
        r[0] = "<if test='offset != null and maxRowNumber != null'> select * from (select t.*, rownum as oracle__rownum__ from (</if>";
        System.arraycopy(query, 0, r, 1, query.length);
        r[r.length - 1] = "<if test='offset != null and maxRowNumber != null'>) t) <where> <if test='offset != null'>oracle__rownum__ &gt; #{offset,jdbcType=NUMERIC}</if> <if test='maxRowNumber != null'>and oracle__rownum__ &lt;= #{maxRowNumber,jdbcType=NUMERIC}</if></where></if>";
        return r;
    }

    @Override
    public String selectPageBeforeSelect() {
        return null;
    }

    @Override
    public String selectPageBeforeWhere() {
        return null;
    }

    @Override
    public String[] envolveInSelectOneRow(String[] query) {
        return query;
    }

    @Override
    public String selectOneRowBeforeSelect() {
        return null;
    }

    @Override
    public String selectOneRowBeforeWhere() {
        return "rownum = 1";
    }

    @Override
    public String subPackage() {
        return "myBatis.oracle";
    }

    @Override
    public String mapperPrefix() {
        return "MyBatisOracle";
    }
    
}
