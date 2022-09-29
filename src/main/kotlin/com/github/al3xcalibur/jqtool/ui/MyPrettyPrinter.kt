package com.github.al3xcalibur.jqtool.ui

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.util.DefaultIndenter
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter
import com.fasterxml.jackson.core.util.Separators

class MyPrettyPrinter : DefaultPrettyPrinter {
    constructor() : super() {
        _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE
        _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE
    }

    constructor(base: DefaultPrettyPrinter) : super(base)

    override fun createInstance(): MyPrettyPrinter {
        check(javaClass == MyPrettyPrinter::class.java) {
            "Failed `createInstance()`: ${javaClass.name} does not override method; it has to"
        }
        return MyPrettyPrinter(this)
    }

    override fun withSeparators(separators: Separators): MyPrettyPrinter {
        this._separators = separators
        this._objectFieldValueSeparatorWithSpaces = separators.objectFieldValueSeparator + " "
        return this
    }

    override fun writeEndArray(g: JsonGenerator, nrOfValues: Int) {
        if (!_arrayIndenter.isInline) {
            --_nesting
        }
        if (nrOfValues > 0) {
            _arrayIndenter.writeIndentation(g, _nesting)
        }
        g.writeRaw(']')
    }

    override fun writeEndObject(g: JsonGenerator, nrOfEntries: Int) {
        if (!_objectIndenter.isInline) {
            --_nesting
        }
        if (nrOfEntries > 0) {
            _objectIndenter.writeIndentation(g, _nesting)
        }
        g.writeRaw('}')
    }
}