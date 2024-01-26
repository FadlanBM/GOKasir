package route

import (
	"github.com/ProjectLSP/internal/controller/PembelianBarang"
	"github.com/ProjectLSP/internal/middleware"
	"github.com/gofiber/fiber/v2"
)

func BarangTranskasiRequest(c fiber.Router) {
	app := c.Group("/pembelianbarang")
	app.Use(middleware.APIKeyAuthMiddleware)
	app.Get("/", PembelianBarang.Index)
	app.Post("/", PembelianBarang.Create)
	app.Get("/:id", PembelianBarang.Detail)
	app.Put("/:id", PembelianBarang.Update)
	app.Delete("/:id", PembelianBarang.Delete)
}
