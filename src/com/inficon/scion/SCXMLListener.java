package com.inficon.scion;

import java.util.List;

public interface SCXMLListener {
    public void onEntry(String stateId);

    public void onExit(String stateId);

    public void onTransition(String sourceStateId, List<String> targetStateIds);
}
