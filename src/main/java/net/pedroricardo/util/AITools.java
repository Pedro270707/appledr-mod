package net.pedroricardo.util;

import io.github.sashirestela.openai.common.function.FunctionDef;

import java.util.List;

public abstract class AITools {
    public abstract List<FunctionDef> getTools();
}
