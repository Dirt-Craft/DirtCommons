package net.dirtcraft.dirtcommons.api.tasks;

import net.dirtcraft.dirtcommons.lib.threads.CommonTask;

import java.util.Collection;

public interface Scheduler {

    void register(Task<?> task);

    void register(Collection<? extends Task<?>> tasks);
}
