package com.comp2042.dto;

import com.comp2042.logic.ClearRow;

/**
 * DownData - Data container for brick down movement results.
 * 
 * Encapsulates:
 * - ClearRow information (if any lines were cleared)
 * - Updated ViewData reflecting new game state after down movement
 * 
 * Used to communicate brick movement and line clear results to UI layer.
 * 
 * @author Umer Imran
 * @version 2.0
 */
public final class DownData {
    private final ClearRow clearRow;
    private final ViewData viewData;

    public DownData(ClearRow clearRow, ViewData viewData) {
        this.clearRow = clearRow;
        this.viewData = viewData;
    }

    public ClearRow getClearRow() {
        return clearRow;
    }

    public ViewData getViewData() {
        return viewData;
    }
}
