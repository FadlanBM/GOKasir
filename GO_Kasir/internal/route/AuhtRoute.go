package route

import (
	"github.com/ProjectLSP/internal/controller"
	"github.com/gofiber/fiber/v2"
)

func AuhtRoute(r fiber.Router) {
	app := r.Group("/auth")

	app.Post("/", controller.AuthController)
}
