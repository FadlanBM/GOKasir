package route

import (
	"github.com/ProjectLSP/internal/controller/Register"
	"github.com/gofiber/fiber/v2"
)

func RegisterKaryawan(c fiber.Router) {
	app := c.Group("/registerKaryawan")
	app.Post("/", Register.RegisterKaryawan)

}
