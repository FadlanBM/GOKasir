package route

import (
	"github.com/ProjectLSP/internal/controller/Karyawan"
	"github.com/gofiber/fiber/v2"
)

func KaryawanRoute(c fiber.Router) {
	app := c.Group("/karyawan")
	/*	app.Use(middleware.APIKeyAuthMiddlewareAdmin)
	 */app.Get("/", Karyawan.Index)
	app.Post("/", Karyawan.Create)
	app.Get("/:id", Karyawan.Detail)
	app.Put("/:id", Karyawan.Update)
	app.Delete("/:id", Karyawan.Delete)
}
