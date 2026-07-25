package me.alex.workflow.checks;

import java.io.File;

public record CheckData<T>(File file, T data) {
}
