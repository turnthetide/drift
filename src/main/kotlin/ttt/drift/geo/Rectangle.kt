package ttt.drift.geo

class Rectangle(id: Long, position: Coords = ORIGIN, direction: Coords = ORIGIN, area: Area) :
    Shape(
        id,
        position,
        direction,
        listOf(
            position,
            position.plusX(area.width),
            position + Coords(area.width, area.height),
            position.plusY(area.height)
        )
    )