package route

import (
	"github.com/ProjectLSP/internal/controller/Voucer"
	"github.com/gofiber/fiber/v2"
)

func VoucerRoute(c fiber.Router) {
	app := c.Group("/voucer")
	/*	app.Use(middleware.APIKeyAuthMiddlewareAdmin)
	 */app.Get("/", Voucer.Index)
	app.Post("/", Voucer.Create)
	app.Get("/:id", Voucer.Detail)
	app.Put("/:id", Voucer.Update)
	app.Delete("/:id", Voucer.Delete)
}
