package route

import (
	"github.com/ProjectLSP/internal/controller/Transaksi"
	"github.com/ProjectLSP/internal/middleware"
	"github.com/gofiber/fiber/v2"
)

func TransaksiRoute(c fiber.Router) {
	app := c.Group("/transaksi")
	app.Use(middleware.APIKeyAuthMiddleware)
	app.Get("/", Transaksi.Index)
	app.Post("/addmember", Transaksi.AddMember)
	app.Post("/calculate", Transaksi.Calculate)
	app.Post("/", Transaksi.Create)
}
