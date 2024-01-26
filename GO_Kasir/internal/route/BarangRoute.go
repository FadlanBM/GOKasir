package route

import (
	"github.com/ProjectLSP/internal/controller/Barang"
	"github.com/gofiber/fiber/v2"
)

func BarangRoute(c fiber.Router) {
	app := c.Group("/barang")
	/*app.Use(middleware.APIKeyAuthMiddlewareAdmin)*/
	app.Get("/", Barang.Index)
	app.Post("/", Barang.Create)
	app.Get("/:id", Barang.Detail)
	app.Get("/WithQr/:code", Barang.DetailQR)
	app.Put("/:id", Barang.Update)
	app.Delete("/:id", Barang.Delete)
}
